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

import com.google.common.collect.Sets;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import dagger.multibindings.IntoSet;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.di.scopes.GameScope;
import org.destinationsol.di.scopes.PauseUnpauseQualifier;
import org.destinationsol.di.scopes.UpdateQualifier;
import org.destinationsol.di.scopes.PauseQualifier;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.ui.TutorialManager;

import java.util.Set;

@Module
public class UpdateSystemModule {

    @Provides
    @IntoSet
    @UpdateQualifier
    @GameScope
    public UpdateAwareSystem provideSoundManager(OggSoundManager soundManager){
        return soundManager;
    }

    @Provides
    @IntoSet
    @PauseUnpauseQualifier
    @GameScope
    public UpdateAwareSystem provideCamera(){
        return new SolCam(0);
    }

    @Provides
    @ElementsIntoSet
    @UpdateQualifier
    @GameScope
    public Set<UpdateAwareSystem> provideTutorialManager(){
        return Sets.newHashSet(new TutorialManager(0, new GameScreens(0, new SolApplication(), null), false, null, null));
    }



}
