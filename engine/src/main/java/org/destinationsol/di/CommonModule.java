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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import dagger.Module;
import dagger.Provides;
import org.destinationsol.GameOptions;
import org.destinationsol.SolFileReader;
import org.destinationsol.game.drawables.CommonDrawer;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class CommonModule {

    @Provides
    @Named("isMobile")
    public boolean provideIsMobile() {
        return Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
    }
}
