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

import com.badlogic.gdx.math.Vector2;
import dagger.BindsInstance;
import dagger.Subcomponent;
import org.destinationsol.di.ShareModule;
import org.destinationsol.di.scope.SolObjectScope;
import org.destinationsol.game.Shard;

import javax.inject.Named;

/**
 * component for creating shard objects
 */
@SolObjectScope
@Subcomponent(modules = ShareModule.class)
public interface SolObjectShardComponent {
    Shard shard();

    @Subcomponent.Builder
    interface Builder{
        SolObjectLootComponent build();
        @BindsInstance SolObjectLootComponent.Builder position(@Named("position") Vector2 position);
        @BindsInstance SolObjectLootComponent.Builder speed(@Named("speed") Vector2 speed);
        @BindsInstance SolObjectLootComponent.Builder rotationSpeed(@Named("size") float sise);
    }
}
