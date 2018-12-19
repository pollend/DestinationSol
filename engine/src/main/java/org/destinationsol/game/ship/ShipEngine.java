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

package org.destinationsol.game.ship;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolTime;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.particle.PartMan;

public class ShipEngine {
    public static final float MAX_RECOVER_ROT_SPD = 5f;
    public static final float RECOVER_MUL = 15f;
    public static final float RECOVER_AWAIT = 2f;

    private final Engine myItem;
    private float myRecoverAwait;
    private final PartMan partMan;

    public ShipEngine(Engine engine, PartMan partMan) {
        this.partMan = partMan;
        this.myItem = engine;
    }

    public void update(SolTime solTime,float angle, Pilot provider, Body body, Vector2 speed, boolean controlsEnabled,
                       float mass, SolShip ship) {

        boolean working = applyInput(solTime, angle, provider, body, speed, controlsEnabled, mass);
        partMan.updateAllHullEmittersOfType(ship, "engine", working);
    }

    private boolean applyInput(SolTime solTime, float shipAngle, Pilot provider, Body body, Vector2 speed,
                               boolean controlsEnabled, float mass) {
        boolean speedOk = SolMath.canAccelerate(shipAngle, speed);
        boolean working = controlsEnabled && provider.isUp() && speedOk;

        Engine e = myItem;
        if (working) {
            Vector2 v = SolMath.fromAl(shipAngle, mass * e.getAcceleration());
            body.applyForceToCenter(v, true);
            SolMath.free(v);
        }

        float rotationSpeed = body.getAngularVelocity() * MathUtils.radDeg;
        float desiredRotationSpeed = 0;
        float rotAcc = e.getRotationAcceleration();
        boolean l = controlsEnabled && provider.isLeft();
        boolean r = controlsEnabled && provider.isRight();
        float absRotationSpeed = SolMath.abs(rotationSpeed);
        if (absRotationSpeed < e.getMaxRotationSpeed() && l != r) {
            desiredRotationSpeed = SolMath.toInt(r) * e.getMaxRotationSpeed();
            if (absRotationSpeed < MAX_RECOVER_ROT_SPD) {
                if (myRecoverAwait > 0) {
                    myRecoverAwait -=  solTime.getTimeStep();
                }
                if (myRecoverAwait <= 0) {
                    rotAcc *= RECOVER_MUL;
                }
            }
        } else {
            myRecoverAwait = RECOVER_AWAIT;
        }
        body.setAngularVelocity(MathUtils.degRad * SolMath.approach(rotationSpeed, desiredRotationSpeed, rotAcc * solTime.getTimeStep()));
        return working;
    }

    public Engine getItem() {
        return myItem;
    }
}
