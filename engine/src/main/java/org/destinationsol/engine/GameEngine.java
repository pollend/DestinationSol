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
package org.destinationsol.engine;

import org.destinationsol.GameState;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.game.context.Context;
import org.destinationsol.ui.SolInputManager;
import org.terasology.entitysystem.core.EntityManager;

public interface GameEngine {

    void initialize();

    void changeState(GameState state);

    boolean update();

    boolean draw();

    /**
     * Request the engine to stop running
     */
    void shutdown();

    /**
     * @return The current state of the engine
     */
    GameState getState();

    Context context();
}
