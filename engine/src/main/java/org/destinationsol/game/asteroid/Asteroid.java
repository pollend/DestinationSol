/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.SpecialSounds;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.SolTime;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.item.MoneyItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.TileObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Asteroid implements SolObject {
    private static final float MIN_SPLIT_SZ = .25f;
    private static final float MIN_BURN_SZ = .3f;
    private static final float SZ_TO_LIFE = 20f;
    private static final float SPD_TO_ATM_DMG = SZ_TO_LIFE * .11f;
    private static final float MAX_SPLIT_SPD = 1f;
    private static final float DUR = .5f;

    private final Body body;
    private final Vector2 position;
    private final Vector2 speed;
    private final ArrayList<Drawable> drawables;
    private final TextureAtlas.AtlasRegion texture;
    private final RemoveController removeController;
    private final DSParticleEmitter smokeSource;
    private final DSParticleEmitter fireSource;
    private final float mass;

    private float angle;
    private float life;
    private float size;

    private final OggSoundManager soundManager;
    private final PartMan partMan;
    private final SpecialEffects specialEffects;
    private final PlanetManager planetManager;
    private final  SpecialSounds specialSounds;
    private final Loot.Factory lootFactory;
    private final Asteroid.Factory asteroidFactory;
    private final ObjectManager objectManager;
    private final ItemManager itemManager;

    Asteroid(SpecialSounds specialSounds, Loot.Factory lootFactory, Factory asteroidFactory,ItemManager itemManager, ObjectManager objectManager, PlanetManager planetManager, PartMan partMan, OggSoundManager soundManager, SpecialEffects specialEffects, TextureAtlas.AtlasRegion tex, Body body, float size, RemoveController removeController, ArrayList<Drawable> drawables) {
        this.itemManager = itemManager;
        this.objectManager = objectManager;
        this.planetManager = planetManager;
        this.specialEffects = specialEffects;
        this.partMan = partMan;
        this.soundManager= soundManager;
        texture = tex;
        this.removeController = removeController;
        this.drawables = drawables;
        this.body = body;
        this.size = size;
        life = SZ_TO_LIFE * size;
        this.specialSounds = specialSounds;
        this.lootFactory = lootFactory;
        this.asteroidFactory = asteroidFactory;
        position = new Vector2();
        speed = new Vector2();
        mass = body.getMass();
        setParamsFromBody();
        List<DSParticleEmitter> effects = specialEffects.buildBodyEffs(size / 2, position, speed);
        smokeSource = effects.get(0);
        fireSource = effects.get(1);
        this.drawables.addAll(smokeSource.getDrawables());
        this.drawables.addAll(fireSource.getDrawables());
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        float rotationSpeed = body.getAngularVelocity();
        return new FarAsteroid(texture, position, angle, removeController, size, speed, rotationSpeed);
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public Vector2 getSpeed() {
        return speed;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, Vector2 collPos) {
        float dmg;
        if (other instanceof TileObject && MIN_BURN_SZ < size) {
            dmg = life;
        } else {
            dmg = absImpulse / mass / DUR;
        }
        receiveDmg(dmg, collPos, DmgType.CRASH);
    }

    @Override
    public String toDebugString() {
        return "Asteroid size: " + size;
    }

    @Override
    public Boolean isMetal() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public void update(SolTime solTime) {
        boolean burning = updateInAtm(solTime);
        smokeSource.setWorking(burning);
        fireSource.setWorking(burning);
        setParamsFromBody();
    }

    private boolean updateInAtm(SolTime solTime) {
        Planet np = planetManager.getNearestPlanet();
        float dst = np.getPosition().dst(position);
        if (np.getFullHeight() < dst) {
            return false;
        }
        if (MIN_BURN_SZ >= size) {
            return false;
        }

        float dmg = body.getLinearVelocity().len() * SPD_TO_ATM_DMG * solTime.getTimeStep();
        receiveDmg(dmg, null, DmgType.FIRE);
        return true;
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        speed.set(body.getLinearVelocity());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    @Override
    public boolean shouldBeRemoved() {
        return life <= 0 || removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public void onRemove() {
        partMan.finish(smokeSource, position);
        partMan.finish(fireSource, position);
        body.getWorld().destroyBody(body);
        if (life <= 0) {
            specialEffects.asteroidDust( position, speed, size);
            float vol = SolMath.clamp(size / .5f);
            soundManager.play(specialSounds.asteroidCrack, null, this, vol);
            maybeSplit();
        }
    }

    private void maybeSplit() {
        if (MIN_SPLIT_SZ > size) {
            return;
        }
        float sclSum = 0;
        while (sclSum < .7f * size * size) {
            float speedAngle = SolRandom.randomFloat(180);
            Vector2 speed = new Vector2();
            SolMath.fromAl(speed, speedAngle, SolRandom.randomFloat(0, .5f) * MAX_SPLIT_SPD);
            speed.add(speed);
            Vector2 newPos = new Vector2();
            SolMath.fromAl(newPos, speedAngle, SolRandom.randomFloat(0, size / 2));
            newPos.add(position);
            float sz = size * SolRandom.randomFloat(.25f, .5f);
            Asteroid a = asteroidFactory.buildNew(newPos, speed, sz, removeController);
            objectManager.addObjDelayed(a);
            sclSum += a.size * a.size;
        }
        float thrMoney = size * 40f * SolRandom.randomFloat(.3f, 1);
        List<MoneyItem> moneyItems = itemManager.moneyToItems(thrMoney);
        for (MoneyItem mi : moneyItems) {
            throwLoot(mi);
        }
    }

    private void throwLoot( SolItem item) {
        float speedAngle = SolRandom.randomFloat(180);
        Vector2 lootSpeed = new Vector2();
        SolMath.fromAl(lootSpeed, speedAngle, SolRandom.randomFloat(0, Loot.MAX_SPD));
        lootSpeed.add(speed);
        Vector2 lootPosition = new Vector2();
        SolMath.fromAl(lootPosition, speedAngle, SolRandom.randomFloat(0, size / 2)); // calculate random offset inside asteroid
        lootPosition.add(position); // add offset to asteroid's position
        Loot l = lootFactory.build( lootPosition, item, lootSpeed, Loot.MAX_LIFE, SolRandom.randomFloat(Loot.MAX_ROT_SPD), null);
        objectManager.addObjDelayed(l);
    }

    @Override
    public void receiveDmg(float dmg, Vector2 position, DmgType dmgType) {
        life -= dmg;
        specialSounds.playHit( this, position, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force, boolean acc) {
        if (acc) {
            force.scl(mass);
        }
        body.applyForceToCenter(force, true);
    }

    public float getLife() {
        return life;
    }

    public static class Factory{
        private static final float DENSITY = 10f;
        private static final float MAX_A_ROT_SPD = .5f;
        private static final float MAX_BALL_SZ = .2f;
        private final CollisionMeshLoader collisionMeshLoader;
        private final List<TextureAtlas.AtlasRegion> textures;

        private final OggSoundManager soundManager;
        private final PartMan partMan;
        private final SpecialEffects specialEffects;
        private final PlanetManager planetManager;
        private final  SpecialSounds specialSounds;
        private final Loot.Factory lootFactory;
        private final Asteroid.Factory asteroidFactory;
        private final ObjectManager objectManager;
        private final ItemManager itemManager;


        @Inject
        public Factory(ObjectManager objectManager, OggSoundManager soundManager, PartMan partMan, SpecialEffects specialEffects, PlanetManager planetManager, SpecialSounds specialSounds, Loot.Factory lootFactory, Factory asteroidFactory, ItemManager itemManager) {
            collisionMeshLoader = new CollisionMeshLoader(objectManager,"engine:asteroids");
            this.soundManager = soundManager;
            this.partMan = partMan;
            this.specialEffects = specialEffects;
            this.planetManager = planetManager;
            this.specialSounds = specialSounds;
            this.lootFactory = lootFactory;
            this.asteroidFactory = asteroidFactory;
            this.itemManager = itemManager;
            textures = Assets.listTexturesMatching("engine:asteroid_.*");
            this.objectManager = objectManager;
        }

        public Body buildBall(Vector2 position, float angle, float rad, float density, boolean sensor) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.angle = angle * MathUtils.degRad;
            bodyDef.angularDamping = 0;
            bodyDef.position.set(position);
            bodyDef.linearDamping = 0;
            Body body = objectManager.getWorld().createBody(bodyDef);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = density;
            fixtureDef.friction = Const.FRICTION;
            fixtureDef.shape = new CircleShape();
            fixtureDef.shape.setRadius(rad);
            fixtureDef.isSensor = sensor;
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
            return body;
        }

        // doesn't consume position
        public Asteroid buildNew(Vector2 position, Vector2 speed, float size, RemoveController removeController) {
            float rotationSpeed = SolRandom.randomFloat(MAX_A_ROT_SPD);
            return build(position, SolRandom.randomElement(textures), size, SolRandom.randomFloat(180), rotationSpeed, speed, removeController);
        }

        // doesn't consume position
        public FarAsteroid buildNewFar(Vector2 position, Vector2 speed, float size, RemoveController removeController) {
            float rotationSpeed = SolRandom.randomFloat(MAX_A_ROT_SPD);
            return new FarAsteroid(SolRandom.randomElement(textures), new Vector2(position), SolRandom.randomFloat(180), removeController, size, new Vector2(speed), rotationSpeed);
        }

        // doesn't consume position
        public Asteroid build( Vector2 position, TextureAtlas.AtlasRegion texture, float size, float angle, float rotationSpeed, Vector2 speed, RemoveController removeController) {

            ArrayList<Drawable> drawables = new ArrayList<>();
            Body body;
            if (MAX_BALL_SZ < size) {
                body = collisionMeshLoader.getBodyAndSprite( texture, size, BodyDef.BodyType.DynamicBody, position, angle, drawables, DENSITY, DrawableLevel.BODIES);
            } else {
                body = buildBall(position, angle, size / 2, DENSITY, false);
                RectSprite s = new RectSprite(texture, size, 0, 0, new Vector2(), DrawableLevel.BODIES, 0, 0, SolColor.WHITE, false);
                drawables.add(s);
            }
            body.setAngularVelocity(rotationSpeed);
            body.setLinearVelocity(speed);

            Asteroid asteroid = new Asteroid(specialSounds,lootFactory,asteroidFactory,itemManager,objectManager,planetManager,partMan,soundManager,specialEffects,texture, body, size, removeController, drawables);
            body.setUserData(asteroid);
            return asteroid;
        }
    }
}

