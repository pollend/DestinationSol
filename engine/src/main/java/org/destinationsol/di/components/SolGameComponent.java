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
package org.destinationsol.di.components;

import dagger.Component;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.di.SolObjectModule;
import org.destinationsol.di.UpdateSystemModule;
import org.destinationsol.di.scopes.GameScope;
import org.destinationsol.di.scopes.PauseUnpauseQualifier;
import org.destinationsol.di.scopes.UpdateQualifier;
import org.destinationsol.di.scopes.PauseQualifier;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.chunk.ChunkProvider;

import java.util.Set;

@Component(dependencies = {SolApplicationComponent.class},modules = {ChunkProvider.class, SolObjectModule.class,UpdateSystemModule.class})
@GameScope
public interface SolGameComponent {

//    Assets assets();
    OggSoundManager soundManager();
    OggMusicManager musicManager();
    ChunkManager chunkManager();

    @UpdateQualifier
    Set<UpdateAwareSystem> updateAwareSystems();

    @PauseQualifier
    Set<UpdateAwareSystem> updatePausedSystems();

    @PauseUnpauseQualifier
    Set<UpdateAwareSystem> updatePauseUnpauseSystem();

}