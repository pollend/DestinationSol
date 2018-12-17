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
import dagger.producers.ProductionComponent;
import org.destinationsol.di.SolObjectLootModule;
import org.destinationsol.di.scope.SolObjectScope;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.*;
import org.destinationsol.game.ship.ShipRepairer;
import org.destinationsol.game.ship.SolShip;

import javax.inject.Named;

@SolObjectScope
@ProductionComponent(modules = SolObjectLootModule.class)
public interface SolObjectShipComponent {
    SolShip ship();

    @ProductionComponent.Builder
    interface Builder{
        SolObjectShipComponent build();
        @BindsInstance
        SolObjectShipComponent.Builder position(@Named("position") Vector2 position);
        @BindsInstance
        SolObjectShipComponent.Builder speed(@Named("speed") Vector2 speed);
        @BindsInstance
        SolObjectShipComponent.Builder angle(@Named("angle") float angle);
        @BindsInstance
        SolObjectShipComponent.Builder rotationSpeed(@Named("rotationSpeed") float rotationalSpeed);
        @BindsInstance
        SolObjectShipComponent.Builder pilot(Pilot pilot);
        @BindsInstance
        SolObjectShipComponent.Builder gun1(@Named("gun1")Gun gun1);
        @BindsInstance
        SolObjectShipComponent.Builder gun2(@Named("gun2")Gun gun2);
        @BindsInstance
        SolObjectShipComponent.Builder removeController(RemoveController removeController);
        @BindsInstance
        SolObjectShipComponent.Builder engine(Engine removeController);
        @BindsInstance
        SolObjectShipComponent.Builder engine(ShipRepairer shipRepairer);
        @BindsInstance
        SolObjectShipComponent.Builder engine(@Named("money")float money);
        @BindsInstance
        SolObjectShipComponent.Builder engine(TradeContainer tradeContainer);
        @BindsInstance
        SolObjectShipComponent.Builder engine(Shield shield);
        @BindsInstance
        SolObjectShipComponent.Builder engine(Armor armor);
    }
}
