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

import dagger.Module;
import dagger.Provides;
import org.destinationsol.di.scopes.GameScope;
import org.destinationsol.CommonDrawer;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.drawables.GameDrawer;

import javax.inject.Singleton;

@Module
public class DrawableModule {
    @Provides
    @Singleton
    public CommonDrawer provideCommonDrawer(){
        return new CommonDrawer();
    }

    @Provides
    @GameScope
    public GameDrawer provideGameDrawer(CommonDrawer commonDrawer){
        return new GameDrawer(commonDrawer);
    }

    @Provides
    @GameScope
    public DrawableManager provideDrawableManager(GameDrawer gameDrawer){
        return new DrawableManager(gameDrawer);
    }


}
