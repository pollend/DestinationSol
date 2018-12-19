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

package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.SpecialSounds;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.di.components.SolObjectComponent;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.SolTime;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.ship.SolShip;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Loot implements SolObject {

    public static final int MAX_ROT_SPD = 4;
    public static final float MAX_SPD = .2f;
    public static final int MAX_LIFE = 6;
    private static final float DURABILITY = 70f;
    private static final float PULL_DESIRED_SPD = 1f;
    private static final float PULL_FORCE = .1f;
    private static final float MAX_OWNER_AWAIT = 4f;
    private final SolItem item;
    private final List<Drawable> drawables;
    private final LightSource lightSource;
    private final Vector2 position;
    private final Body body;
    private final float mass;

    private SolShip owner;
    private float ownerAwait;
    private int life;
    private float angle;

    @Inject
    PartMan partMan;
    @Inject
    SpecialSounds specialSounds;
    @Inject
    OggSoundManager soundManager;
    @Inject
    ObjectManager objectManager;

    public Loot(SolItem item, Body body, int life, List<Drawable> drawables, LightSource ls, SolShip owner) {

        this.body = body;
        this.life = life;
        this.item = item;
        this.drawables = drawables;
        lightSource = ls;
        this.owner = owner;
        ownerAwait = MAX_OWNER_AWAIT;
        position = new Vector2();
        mass = this.body.getMass();
        setParamsFromBody();
    }

    @Override
    public void update(SolTime time) {
        setParamsFromBody();
        lightSource.update(true, angle, time);
        if (ownerAwait > 0) {
            ownerAwait -= time.getTimeStep();
            if (ownerAwait <= 0) {
                owner = null;
            }
        }
        SolShip puller = null;
        float minDist = Float.MAX_VALUE;
        List<SolObject> objs = objectManager.getObjects();
        for (SolObject o : objs) {
            if (!(o instanceof SolShip)) {
                continue;
            }
            SolShip ship = (SolShip) o;
            if (!ship.getPilot().collectsItems()) {
                continue;
            }
            if (!(item instanceof MoneyItem) && !ship.getItemContainer().canAdd(item)) {
                continue;
            }
            float dst = ship.getPosition().dst(position);
            if (minDist < dst) {
                continue;
            }
            puller = ship;
            minDist = dst;
        }
        if (puller != null) {
            maybePulled(puller, puller.getPosition(), puller.getPullDist());
        }
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    @Override
    public boolean shouldBeRemoved() {
        return life <= 0;
    }

    @Override
    public void onRemove() {
        body.getWorld().destroyBody(body);
    }

    @Override
    public void receiveDmg(float dmg, Vector2 position, DmgType dmgType) {
        life -= dmg;
        specialSounds.playHit(this, position, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force,  boolean acc) {
        if (acc) {
            force.scl(mass);
        }
        body.applyForceToCenter(force, true);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return null;
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
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, Vector2 collPos) {
        float dmg = absImpulse / mass / DURABILITY;
        receiveDmg((int) dmg, collPos, DmgType.CRASH);
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public void maybePulled(SolShip ship, Vector2 pullerPos, float radius) {
        if (ship == owner) {
            return;
        }
        Vector2 toPuller = SolMath.getVec(pullerPos);
        toPuller.sub(getPosition());
        float pullerDist = toPuller.len();
        if (0 < pullerDist && pullerDist < radius) {
            toPuller.scl(PULL_DESIRED_SPD / pullerDist);
            Vector2 speed = body.getLinearVelocity();
            Vector2 speedDiff = SolMath.distVec(speed, toPuller);
            float speedDiffLen = speedDiff.len();
            if (speedDiffLen > 0) {
                speedDiff.scl(PULL_FORCE / speedDiffLen);
                body.applyForceToCenter(speedDiff, true);
            }
            SolMath.free(speedDiff);
        }
        SolMath.free(toPuller);
    }

    public SolItem getItem() {
        return life > 0 ? item : null;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public SolShip getOwner() {
        return owner;
    }

    public void pickedUp( SolShip ship) {
        life = 0;
        Vector2 speed = new Vector2(ship.getPosition());
        speed.sub(position);
        float fadeTime = .25f;
        speed.scl(1 / fadeTime);
        speed.add(ship.getSpeed());
        partMan.blip( position, angle, item.getItemType().sz, fadeTime, speed, item.getIcon());
        soundManager.play(item.getItemType().pickUpSound, null, this);
    }

    public static class Factory
    {
        private final SolCam cam;
        private final ObjectManager objectManager;
        private final SolObjectComponent objectComponent;

        @Inject
        public Factory(SolCam cam, ObjectManager objectManager, SolObjectComponent solObjectComponent){
            this.cam = cam;
            this.objectManager = objectManager;
            this.objectComponent = solObjectComponent;
        }

        // set speed & rot speed
        public Loot build(Vector2 position, SolItem item, Vector2 speed, int life, float rotationSpeed, SolShip owner) {
            List<Drawable> drawables = new ArrayList<>();
            TextureAtlas.AtlasRegion tex = item.getIcon();
            float sz = item.getItemType().sz;
            RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DrawableLevel.GUNS, 0, 0, SolColor.WHITE, false);
            drawables.add(s);
            Body b = buildBody( position, sz);
            b.setLinearVelocity(speed);
            b.setAngularVelocity(rotationSpeed);
            Color col = item.getItemType().color;
            LightSource ls = new LightSource(cam,sz + .18f, false, .5f, new Vector2(), col);
            ls.collectDrawables(drawables);
            Loot loot = new Loot(item, b, life, drawables, ls, owner);
            objectComponent.inject(loot);
            b.setUserData(loot);
            return loot;
        }

        private Body buildBody(Vector2 position, float sz) {
            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.DynamicBody;
            bd.angle = 0;
            bd.angularDamping = 0;
            bd.position.set(position);
            bd.linearDamping = 0;
            Body body = objectManager.getWorld().createBody(bd);
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(sz / 2, sz / 2);
            body.createFixture(shape, .5f);
            shape.dispose();
            return body;
        }

    }
}
