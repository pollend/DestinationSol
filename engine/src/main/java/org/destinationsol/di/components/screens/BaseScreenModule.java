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
import dagger.Provides;
import org.destinationsol.di.scope.ActivityScope;
import org.destinationsol.game.screens.RightPaneLayout;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.SolUiScreen;

@Module
public class BaseScreenModule<T extends SolUiScreen> {

    protected final T screen;

    public BaseScreenModule(T screen){
        this.screen = screen;
    }

    @Provides
    @ActivityScope
    public T provideScreen(){
        return screen;
    }
}
