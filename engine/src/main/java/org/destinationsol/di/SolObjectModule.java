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

import com.google.auto.factory.Provided;
import dagger.Module;
import dagger.Provides;
import org.destinationsol.game.ShardBuilder;
import org.destinationsol.game.StarPort;
import org.destinationsol.game.asteroid.AsteroidBuilder;
import org.destinationsol.game.ship.ShipBuilder;

@Module
public class SolObjectModule {

    @Provides
    public AsteroidBuilder provideAsteroidBuilder(){
        return new AsteroidBuilder();
    }

    @Provides
    public ShipBuilder provideShipBuilder(){return new ShipBuilder();}

    @Provides
    public ShardBuilder provideShardBuilder(){return new ShardBuilder();}

    @Provides
    public StarPort.Builder provideStartPortBuilder() {
        return new StarPort.Builder();
    }
}
