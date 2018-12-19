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

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.destinationsol.di.components.SolObjectComponent;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.item.Loot;

@Module
public abstract class SolObjectModule {
    @Provides
    static Loot.Factory provideLootFactory(SolCam cam, ObjectManager objectManager, SolObjectComponent solObjectComponent){
        return new Loot.Factory(cam,objectManager,solObjectComponent);
    }

    @Binds
    public abstract SolObject bindLoot(Loot loot);

}
