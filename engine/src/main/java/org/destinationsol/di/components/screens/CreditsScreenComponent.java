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
package org.destinationsol.di.components.screens;

import dagger.Module;
import dagger.Subcomponent;
import org.destinationsol.di.scope.ActivityScope;
import org.destinationsol.menu.CreditsScreen;

@ActivityScope
@Subcomponent(
        modules = CreditsScreenComponent.CreditsScreenModule.class
)
public interface CreditsScreenComponent {
    CreditsScreen screen();

    @Module
    class CreditsScreenModule extends BaseScreenModule<CreditsScreen>{
        public CreditsScreenModule(CreditsScreen screen) {
            super(screen);
        }
    }
}
