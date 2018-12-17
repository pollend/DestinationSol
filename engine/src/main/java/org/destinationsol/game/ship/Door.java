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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.SpecialSounds;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.*;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.input.Pilot;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Door {
    public static final float SPD_LEN = .4f;
    public static final float SENSOR_DIST = 3f;
    public static final float DOOR_LEN = 1.1f;
    public static final float MAX_OPEN_AWAIT = DOOR_LEN / SPD_LEN;
    private final PrismaticJoint myJoint;
    private final RectSprite myS;
    private float myOpenAwait;

    @Inject
    ObjectManager objectManager;
    @Inject
    FactionManager factionManager;
    @Inject
    OggSoundManager soundManager;
    @Inject
    SpecialSounds specialSounds;

    public Door(PrismaticJoint joint, RectSprite s) {
        myJoint = joint;
        myS = s;
    }

    public void update(SolTime solTime, SolShip ship) {
        Vector2 doorPos = getBody().getPosition();
        boolean open = myOpenAwait <= 0 && shouldOpen(ship, doorPos);
        if (open) {
            myOpenAwait = MAX_OPEN_AWAIT;
            myJoint.setMotorSpeed(SPD_LEN);
            soundManager.play(specialSounds.doorMove, doorPos, ship);
        } else if (myOpenAwait > 0) {
            myOpenAwait -= solTime.getTimeStep();
            if (myOpenAwait < 0) {
                myJoint.setMotorSpeed(-SPD_LEN);
                soundManager.play(specialSounds.doorMove, doorPos, ship);
            }
        }

        Vector2 shipPos = ship.getPosition();
        float shipAngle = ship.getAngle();
        SolMath.toRel(doorPos, myS.getRelativePosition(), shipAngle, shipPos);
    }

    private boolean shouldOpen(SolShip ship, Vector2 doorPos) {
        Faction faction = ship.getPilot().getFaction();
        List<SolObject> objs = objectManager.getObjects();
        for (SolObject o : objs) {
            if (o == ship) {
                continue;
            }
            if (!(o instanceof SolShip)) {
                continue;
            }
            SolShip ship2 = (SolShip) o;
            Pilot pilot2 = ship2.getPilot();
            if (!pilot2.isUp()) {
                continue;
            }
            if (factionManager.areEnemies(pilot2.getFaction(), faction)) {
                continue;
            }
            if (ship2.getPosition().dst(doorPos) < SENSOR_DIST) {
                return true;
            }
        }
        return false;
    }

    public void collectDras(ArrayList<Drawable> drawables) {
        drawables.add(myS);
    }

    public Body getBody() {
        return myJoint.getBodyB();
    }

    public void onRemove() {
        World w = objectManager.getWorld();
        Body doorBody = getBody();
        w.destroyJoint(myJoint);
        w.destroyBody(doorBody);
    }
}
