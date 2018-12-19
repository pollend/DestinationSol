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
import org.destinationsol.CommonDrawer;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.SpecialSounds;
import org.destinationsol.di.Qualifier.NewGame;
import org.destinationsol.di.Qualifier.ShipName;
import org.destinationsol.di.Qualifier.Tutorial;
import org.destinationsol.di.components.SolGameComponent;
import org.destinationsol.di.scope.GameScope;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.*;
import org.destinationsol.game.chunk.ChunkFiller;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.drawables.DrawableDebugger;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.farBg.FarBackgroundManagerOld;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.TutorialManager;

import javax.inject.Named;
import java.util.Optional;

@Module
public class GameModule {
    @Provides
    @GameScope
    static SpecialSounds provideSpecialSounds(OggSoundManager soundManager) {
        return new SpecialSounds(soundManager);
    }


    @GameScope
    @Provides
    static SolGame provideSolGame(SolGameComponent gameComponent,
                                  @ShipName String shipName,
                                  @Tutorial boolean tut,
                                  @NewGame boolean isNewGame) {
        return new SolGame(gameComponent, shipName, tut, isNewGame);
    }

    @GameScope
    @Provides
    static ItemManager provideItemManager(OggSoundManager oggSoundManager, EffectTypes effectTypes, GameColors colors) {
        return new ItemManager(oggSoundManager, effectTypes, colors);
    }


    @GameScope
    @Provides
    static BeaconHandler provideBeaconHandler() {
        return new BeaconHandler();
    }

    @GameScope
    @Provides
    static AbilityCommonConfigs provideAbilityConfig(EffectTypes effectTypes, GameColors cols, OggSoundManager soundManager) {
        return new AbilityCommonConfigs(effectTypes, cols, soundManager);
    }

    @GameScope
    @Provides
    static HullConfigManager provideHullConfigProvider(ItemManager itemManager, AbilityCommonConfigs abilityCommonConfigs) {
        return new HullConfigManager(itemManager, abilityCommonConfigs);
    }

    @GameScope
    @Provides
    static PlanetManager providePlanetManager(SolTime time,ObjectManager objectManager,HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager,SolCam cam) {
        return new PlanetManager(time,objectManager,hullConfigs, cols, itemManager,cam);
    }

    @GameScope
    @Provides
    static SolContactListener provideSolContactListener(SolGame game) {
        return new SolContactListener(game);
    }


    @GameScope
    @Provides
    static FactionManager provideFactionManager() {
        return new FactionManager();
    }

    @GameScope
    @Provides
    static ObjectManager provideObjectManager(SolContactListener contactListener, FactionManager factionManager) {
        return new ObjectManager(contactListener, factionManager);
    }

    @GameScope
    @Provides
    static Optional<TutorialManager> provideTutorialManager(@Tutorial boolean isTut, GameScreens gameScreens, GameOptions gameOptions, SolGame solGame) {
        if (isTut)
            return Optional.of(new TutorialManager(gameScreens, isTut, gameOptions, solGame));
        return Optional.empty();
    }

    @Provides
    @GameScope
    static GalaxyFiller provideGalaxyFiller(HullConfigManager hullConfigManager) {
        return new GalaxyFiller(hullConfigManager);
    }


    @GameScope
    @Provides
    static ChunkManager provideChunkManager(SolCam cam, ChunkFiller chunkFiller) {
        return new ChunkManager(cam,chunkFiller);
    }

    @Provides
    @GameScope
    static StarPort.Builder provideStarportBuilder() {
        return new StarPort.Builder();
    }

    @Provides
    @GameScope
    static Hero provideHero(@Named("hero") SolShip shipHero) {
        return new Hero(shipHero);
    }

    @Provides
    @GameScope
    static ShipBuilder provideShipBuilder() {
        return null;
    }

    @Provides
    @GameScope
    static GameScreens provideGameScreens(SolApplication cmp, Context context) {
        return new GameScreens(cmp, context);
    }

    @GameScope
    @Provides
    static GameDrawer provideGameDrawer(CommonDrawer commonDrawer) {
        return new GameDrawer(commonDrawer);
    }

    @GameScope
    @Provides
    static MapDrawer provideMapDrawer(GridDrawer gridDrawer) {
        return new MapDrawer(gridDrawer);
    }

    @GameScope
    @Provides
    static GridDrawer provideGridDrawer() {
        return new GridDrawer();
    }

    @GameScope
    @Provides
    static SolCam provideSolCam() {
        return new SolCam();
    }

    @GameScope
    @Provides
    static DrawableDebugger provideDrawableDebugger() {
        return new DrawableDebugger();
    }

    @GameScope
    @Provides
    static GameColors provideGameColors() {
        return new GameColors();
    }

    @GameScope
    @Provides
    static MountDetectDrawer provideMountDetectDrawer() {
        return new MountDetectDrawer();
    }

    @GameScope
    @Provides
    static PartMan providePartMan() {
        return new PartMan();
    }

    @GameScope
    @Provides
    static DrawableManager provideDrawableManager(GameDrawer gameDrawer,OggSoundManager oggSoundManager) {
        return new DrawableManager(gameDrawer,oggSoundManager);
    }

    @GameScope
    @Provides
    static FarBackgroundManagerOld provideFarBackgroundManager() {
        return new FarBackgroundManagerOld();
    }

    @GameScope
    @Provides
    static EffectTypes provideEffectTypes() {
        return new EffectTypes();
    }

    @GameScope
    @Provides
    static SpecialEffects provideSpecialEffects(SolCam cam,OggSoundManager soundManager, PlanetManager planetManager, PartMan partMan, EffectTypes effectTypes, GameColors colors) {
        return new SpecialEffects(cam,soundManager,planetManager,partMan,effectTypes, colors);
    }

    @GameScope
    @Provides
    static SolTime provideTime(){
        return new SolTime();

    }

}
