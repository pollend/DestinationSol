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
package org.destinationsol.modes;

import org.destinationsol.GameOptions;
import org.destinationsol.GameState;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.engine.GameEngine;

public class MainMenuState  implements GameState {
    @Override
    public void init(GameEngine engine) {
        GameOptions gameOptions =  engine.context().get(GameOptions.class);
        engine.context().get(OggMusicManager.class).playMusic(OggMusicManager.MENU_MUSIC_SET, gameOptions);
    }

    @Override
    public void dispose(boolean shuttingDown) {

    }

    @Override
    public void handleInput(float delta) {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw() {

    }

    @Override
    public void render() {

    }

    @Override
    public boolean isHibernationAllowed() {
        return false;
    }

    @Override
    public String getLoggingPhase() {
        return null;
    }
}
