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
package org.destinationsol.game;

import org.destinationsol.Const;
import org.destinationsol.di.Qualifier.OnPauseUpdate;
import org.destinationsol.di.Qualifier.OnUpdate;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.SloMo;

import javax.inject.Inject;
import java.util.Set;

public class UpdateSystem {
    private float timeStep;
    private float time;
    private boolean paused;
    private float timeFactor;

    @Inject
    @OnUpdate
    Set<UpdateAwareSystem> updateSystems;
    @Inject
    @OnPauseUpdate
    Set<UpdateAwareSystem> onPausedUpdateSystems;
    @Inject
    SolGame solGame;

    public UpdateSystem(){
    }

    public void update() {
        if (paused) {
            onPausedUpdateSystems.forEach(system -> system.update(timeStep));
        } else {
            updateTime();
            updateSystems.forEach(system -> system.update(timeStep));
        }
    }

    public boolean isPaused() {
        return paused;
    }

    private void updateTime() {
        scaleTimeStep();
        time += timeStep;
    }

    public float getTimeStep() {
        return timeStep;
    }


    private void scaleTimeStep() {
        timeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
        if (solGame.getHero().isAlive() && solGame.getHero().isNonTranscendent()) {
            ShipAbility ability = solGame.getHero().getAbility();
            if (ability instanceof SloMo) {
                float factor = ((SloMo) ability).getFactor();
                timeFactor *= factor;
            }
        }
        timeStep = Const.REAL_TIME_STEP * timeFactor;
    }
}
