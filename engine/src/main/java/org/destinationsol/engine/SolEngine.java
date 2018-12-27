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
import org.destinationsol.ModuleManager;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.game.context.Context;
import org.destinationsol.ui.SolInputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetTypeManager;
import org.terasology.entitysystem.core.EntityManager;

public class SolEngine implements GameEngine {

    private static final Logger logger = LoggerFactory.getLogger(SolEngine.class);

    private GameState currentState;

    //TODO: remove
    private final Context context;

    public SolEngine(EngineFactory engineFactory){
        this.context = engineFactory.context();
        configureModules(engineFactory.assetTypeManager());
    }

    private void configureModules(AssetTypeManager assetTypeManager){
        ModuleManager moduleManager = new ModuleManager();

    }

    @Override
    public void initialize() {

    }

    @Override
    public void changeState(GameState newState) {
        if (currentState != null) {
            currentState.dispose();
        }
        currentState = newState;
        newState.init(this);

    }

    @Override
    public boolean update() {
        if(currentState == null){
            return false;
        }
        currentState.update(0);
        return true;
    }

    @Override
    public boolean draw() {
        if(currentState == null){
            return false;
        }
        currentState.draw();
        return true;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public GameState getState() {
        return null;
    }

    @Override
    public Context context() {
        return context;
    }


}
