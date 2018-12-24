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
package org.destinationsol.entitySystem.entityManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.terasology.entitysystem.component.ComponentManager;
import org.terasology.entitysystem.core.EntityManager;
import org.terasology.entitysystem.entity.inmemory.InMemoryEntityManager;
import org.terasology.entitysystem.transaction.TransactionManager;

@Module(includes = InMemoryEntityManagerModule.BindInMemoryEntityManager.class)
public class InMemoryEntityManagerModule {
    private final ComponentManager componentManager;
    private final TransactionManager transactionManager;
    public InMemoryEntityManagerModule(ComponentManager componentManager,TransactionManager transactionManager){
        this.componentManager = componentManager;
        this.transactionManager = transactionManager;
    }

    @Provides
    public InMemoryEntityManager provideInMemoryEntityManager(){
        return new InMemoryEntityManager(componentManager,transactionManager);
    }

    @Module
    interface BindInMemoryEntityManager {
        @Binds
        EntityManager bindEntityManager(InMemoryEntityManager entityManager);
    }
}

