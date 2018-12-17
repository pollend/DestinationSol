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
package org.destinationsol.di;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.utils.JsonValue;
import dagger.Module;
import dagger.Provides;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.di.components.SolObjectDoorComponent;
import org.destinationsol.di.scope.SolObjectScope;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.gun.GunMount;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.*;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.ship.*;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Module
public class SolObjectShipModule {

    @Provides
    @SolObjectScope
    static FarShip provideFarShip() {
        return null;
    }

    @Provides
    @SolObjectScope
    static SolShip provideShip(SolObjectDoorComponent.Builder doorBuilder,@Named("position") Vector2 position,@Named("speed") Vector2 speed, @Named("angle")float angle, @Named("rotationSpeed")float rotationSpeed, Pilot pilot,
                               ItemContainer container, HullConfig hullConfig, @Named("life")float life, @Named("gun1")Gun gun1,
                               @Named("gun2")Gun gun2, RemoveController removeController, Engine engine,
                               ShipRepairer repairer, @Named("money")float money, TradeContainer tradeContainer, Shield shield,
                               Armor armor){
        ArrayList<Drawable> drawables = new ArrayList<>();
//        Hull hull = buildHull(game, position, speed, angle, rotationSpeed, hullConfig, life, drawables);
//        SolShip ship = new SolShip(game, pilot, hull, removeController, drawables, container, repairer, money, tradeContainer, shield, armor);
//        hull.getBody().setUserData(ship);
//        for (Door door : hull.getDoors()) {
//            door.getBody().setUserData(ship);
//        }
//
//        hull.setParticleEmitters(game, ship);
//
//        if (engine != null) {
//            hull.setEngine(engine);
//        }
//        if (gun1 != null) {
//            GunMount gunMount0 = hull.getGunMount(false);
//            if (gunMount0.isFixed() == gun1.config.fixed) {
//                gunMount0.setGun(game, ship, gun1, hullConfig.getGunSlot(0).isUnderneathHull(), 1);
//            }
//        }
//        if (gun2 != null) {
//            GunMount gunMount1 = hull.getGunMount(true);
//            if (gunMount1 != null) {
//                if (gunMount1.isFixed() == gun2.config.fixed) {
//                    gunMount1.setGun(game, ship, gun2, hullConfig.getGunSlot(1).isUnderneathHull(), 2);
//                }
//            }
//        }
//        return ship;
        return null;
    }

//    private Hull buildHull(SolGame game, Vector2 position, Vector2 speed, float angle, float rotationSpeed, HullConfig hullConfig,
//                           float life, ArrayList<Drawable> drawables) {
//        //TODO: This logic belongs in the HullConfigManager/HullConfig
//        String shipName = hullConfig.getInternalName();
//
//        Json json = Assets.getJson(shipName);
//
//        JsonValue rigidBodyNode = json.getJsonValue().get("rigidBody");
//        myCollisionMeshLoader.readRigidBody(rigidBodyNode, hullConfig);
//
//        json.dispose();
//
//        BodyDef.BodyType bodyType = hullConfig.getType() == HullConfig.Type.STATION ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.DynamicBody;
//        DrawableLevel level = hullConfig.getType() == HullConfig.Type.STD ? DrawableLevel.BODIES : hullConfig.getType() == HullConfig.Type.BIG ? DrawableLevel.BIG_BODIES : DrawableLevel.STATIONS;
//        Body body = myCollisionMeshLoader.getBodyAndSprite(game, hullConfig, hullConfig.getSize(), bodyType, position, angle,
//                drawables, SHIP_DENSITY, level, hullConfig.getTexture());
//        Fixture shieldFixture = createShieldFixture(hullConfig, body);
//
//        GunMount gunMount0 = new GunMount(hullConfig.getGunSlot(0));
//        GunMount gunMount1 = (hullConfig.getNrOfGunSlots() > 1)
//                ? new GunMount(hullConfig.getGunSlot(1))
//                : null;
//
//        List<LightSource> lCs = new ArrayList<>();
//        for (Vector2 p : hullConfig.getLightSourcePositions()) {
//            LightSource lc = new LightSource(.35f, true, .7f, p, game.getCols().hullLights);
//            lc.collectDrawables(drawables);
//            lCs.add(lc);
//        }
//
//        ArrayList<ForceBeacon> beacons = new ArrayList<>();
//        for (Vector2 relPos : hullConfig.getForceBeaconPositions()) {
//            ForceBeacon fb = new ForceBeacon(game, relPos, position, speed);
//            fb.collectDras(drawables);
//            beacons.add(fb);
//        }
//
//        ArrayList<Door> doors = new ArrayList<>();
//        for (Vector2 doorRelPos : hullConfig.getDoorPositions()) {
//            Door door = createDoor(game, position, angle, body, doorRelPos);
//            door.collectDras(drawables);
//            doors.add(door);
//        }
//
//        Fixture base = getBase(hullConfig.hasBase(), body);
//        Hull hull = new Hull(game, hullConfig, body, gunMount0, gunMount1, base, lCs, life, beacons, doors, shieldFixture);
//        body.setLinearVelocity(speed);
//        body.setAngularVelocity(rotationSpeed * MathUtils.degRad);
//        return hull;
//    }
//
//    private Fixture createShieldFixture(HullConfig hullConfig, Body body) {
//        CircleShape shieldShape = new CircleShape();
//        shieldShape.setRadius(Shield.SIZE_PERC * hullConfig.getSize());
//        FixtureDef shieldDef = new FixtureDef();
//        shieldDef.shape = shieldShape;
//        shieldDef.isSensor = true;
//        Fixture shieldFixture = body.createFixture(shieldDef);
//        shieldShape.dispose();
//        return shieldFixture;
//    }
//
//    private Door createDoor(SolGame game, Vector2 position, float angle, Body body, Vector2 doorRelPos) {
//        World w = game.getObjectManager().getWorld();
//        TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion("engine:door");
//        PrismaticJoint joint = createDoorJoint(body, w, position, doorRelPos, angle);
//        RectSprite s = new RectSprite(tex, Door.DOOR_LEN, 0, 0, new Vector2(doorRelPos), DrawableLevel.BODIES, 0, 0, SolColor.WHITE, false);
//        return new Door(joint, s);
//    }
//
//    private PrismaticJoint createDoorJoint(Body shipBody, World w, Vector2 shipPos, Vector2 doorRelPos, float shipAngle) {
//        Body doorBody = createDoorBody(w, shipPos, doorRelPos, shipAngle);
//        PrismaticJointDef jd = new PrismaticJointDef();
//        jd.initialize(shipBody, doorBody, shipPos, Vector2.Zero);
//        jd.localAxisA.set(1, 0);
//        jd.collideConnected = false;
//        jd.enableLimit = true;
//        jd.enableMotor = true;
//        jd.lowerTranslation = 0;
//        jd.upperTranslation = Door.DOOR_LEN;
//        jd.maxMotorForce = 2;
//        return (PrismaticJoint) w.createJoint(jd);
//    }
//
//    private Body createDoorBody(World world, Vector2 shipPos, Vector2 doorRelPos, float shipAngle) {
//        BodyDef bd = new BodyDef();
//        bd.type = BodyDef.BodyType.DynamicBody;
//        bd.angle = shipAngle * MathUtils.degRad;
//        bd.angularDamping = 0;
//        bd.linearDamping = 0;
//        SolMath.toWorld(bd.position, doorRelPos, shipAngle, shipPos);
//        Body body = world.createBody(bd);
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(Door.DOOR_LEN / 2, .03f);
//        body.createFixture(shape, SHIP_DENSITY);
//        shape.dispose();
//        return body;
//    }
//
//    public Vector2 getOrigin(String name) {
//        return myCollisionMeshLoader.getOrigin(name + ".png", 1);
//    }
}
