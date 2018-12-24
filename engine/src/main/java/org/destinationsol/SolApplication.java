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
package org.destinationsol;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.di.AppModule;
import org.destinationsol.di.components.DaggerSolApplicationComponent;
import org.destinationsol.di.components.DaggerSolGameComponent;
import org.destinationsol.di.components.SolApplicationComponent;
import org.destinationsol.di.components.SolGameComponent;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SaveManager;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.menu.MenuScreens;
import org.destinationsol.ui.DebugCollector;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.ResizeSubscriber;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class SolApplication implements ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(SolApplication.class);

    @Inject
    ModuleManager moduleManager;
    @Inject
    OggMusicManager musicManager;
    @Inject
    OggSoundManager soundManager;
    @Inject
    SolInputManager inputManager;
    @Inject
    GameOptions options;
    @Inject
    CommonDrawer commonDrawer;
    @Inject
    SolLayouts layouts;
    @Inject
    UiDrawer uiDrawer;

    private String fatalErrorMsg;
    private String fatalErrorTrace;

    private float timeAccumulator = 0;

    private SolApplicationComponent applicationComponent;

    // TODO: Make this non-static.
    private static Set<ResizeSubscriber> resizeSubscribers;

    @Inject
    public SolApplication() {
        // Initiate Box2D to make sure natives are loaded early enough
        Box2D.init();

    }

    @Override
    public void create() {
        resizeSubscribers = new HashSet<>();

        this.applicationComponent = DaggerSolApplicationComponent.builder()
                .appModule(new AppModule(null))
                .build();
        Assets.initialize(applicationComponent.moduleEnviroment());
        applicationComponent.inject(this);
        this.applicationComponent.displayDimensions().set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        boolean isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
        if (isMobile) {
            DebugOptions.read(null);
        }

        logger.info("\n\n ------------------------------------------------------------ \n");
        moduleManager.printAvailableModules();
        musicManager.playMusic(OggMusicManager.MENU_MUSIC_SET, options);

        inputManager.setScreen(this, applicationComponent.menuScreens().main);
    }

    @Override
    public void resize(int newWidth, int newHeight) {

        applicationComponent.displayDimensions().set(newWidth, newHeight);

        for (ResizeSubscriber resizeSubscriber : resizeSubscribers) {
            resizeSubscriber.resize();
        }
    }

    public void render() {
        timeAccumulator += Gdx.graphics.getDeltaTime();

        while (timeAccumulator > Const.REAL_TIME_STEP) {
            safeUpdate();
            timeAccumulator -= Const.REAL_TIME_STEP;
        }

        draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    private void safeUpdate() {
        if (fatalErrorMsg != null) {
            return;
        }

        try {
            update();
        } catch (Throwable t) {
            logger.error("Fatal Error:", t);
            fatalErrorMsg = "A fatal error occurred:\n" + t.getMessage();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            fatalErrorTrace = sw.toString();

            if (!applicationComponent.isMobile()) {
                throw t;
            }
        }
    }

    private void update() {
        DebugCollector.update();

        if (DebugOptions.SHOW_FPS) {
            DebugCollector.debug("Fps", Gdx.graphics.getFramesPerSecond());
        }

        inputManager.update();

        if (solGame != null) {
            solGame.update();
        }

        SolMath.checkVectorsTaken(null);
    }

    private void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        commonDrawer.begin();
        if (solGame != null) {
            solGame.draw();
        }
        uiDrawer.updateMtx();
        inputManager.draw(uiDrawer, this);
        if (solGame != null) {
            solGame.drawDebugUi(uiDrawer);
        }
        if (fatalErrorMsg != null) {
            uiDrawer.draw(uiDrawer.whiteTexture, displayDimensions.getRatio(), .5f, 0, 0, 0, .25f, 0, SolColor.UI_BG);
            uiDrawer.drawString(fatalErrorMsg, displayDimensions.getRatio(), .5f, FontSize.MENU, true, SolColor.WHITE);
            uiDrawer.drawString(fatalErrorTrace, .2f * displayDimensions.getRatio(), .6f, FontSize.DEBUG, false, SolColor.WHITE);
        }
        DebugCollector.draw(uiDrawer);
        if (solGame == null) {
            uiDrawer.drawString("v" + Const.VERSION, 0.01f, .974f, FontSize.DEBUG, UiDrawer.TextAlignment.LEFT, false, SolColor.WHITE);
        }
        commonDrawer.end();
    }

    public void loadGame(boolean tut, String shipName, boolean isNewGame) {
        if (solGame != null) {
            throw new AssertionError("Starting a new game with unfinished current one");
        }

        inputManager.setScreen(this, applicationComponent.menuScreens().loading);
        applicationComponent.menuScreens().loading.setMode(tut, shipName, isNewGame);
        musicManager.playMusic(OggMusicManager.GAME_MUSIC_SET, options);
    }

    public void play(boolean tut, String shipName, boolean isNewGame) {
        if (isNewGame) {
            beforeNewGame();
        } else {
            beforeLoadGame();
        }
        SolGameComponent gameComponent =  DaggerSolGameComponent.builder()
                .newGame(isNewGame)
                .shipName(shipName)
                .tutorial(tut)
                .setApplicationComponent(applicationComponent).build();
        solGame = gameComponent.game();
        solGame.initilize();

//        solGame = new SolGame(shipName, tut, isNewGame, commonDrawer, context, worldConfig);
        inputManager.setScreen(this, solGame.getScreens().mainGameScreen);
        musicManager.playMusic(OggMusicManager.GAME_MUSIC_SET, options);
    }

    public SolInputManager getInputManager() {
        return inputManager;
    }

    public MenuScreens getMenuScreens() {
        return applicationComponent.menuScreens();
    }

    public void dispose() {
        commonDrawer.dispose();

        if (solGame != null) {
            solGame.onGameEnd();
        }

        inputManager.dispose();
    }


    public SolLayouts getLayouts() {
        return layouts;
    }

    public void finishGame() {
        solGame.onGameEnd();
        solGame = null;
        inputManager.setScreen(applicationComponent.menuScreens().main);
    }

    public boolean isMobile() {
        return applicationComponent.isMobile();
    }

    public GameOptions getOptions() {
        return options;
    }

    public OggMusicManager getMusicManager() {
        return musicManager;
    }

    public OggSoundManager getSoundManager() {
        return soundManager;
    }


     /** This method is called when the "New Game" button gets pressed. It sets the seed for random generation, and the number of systems */
    private void beforeNewGame() {
        // Reset the seed so this galaxy isn't the same as the last
        applicationComponent.worldConfig().setSeed(System.currentTimeMillis());
        SolRandom.setSeed(applicationComponent.worldConfig().getSeed());

        applicationComponent.worldConfig().setNumberOfSystems(getMenuScreens().newShip.getNumberOfSystems());
    }

    /**
     * This method is called when the "Continue" button gets pressed. It loads the world file to get the seed used for the world generation, and the number of systems
     */
    private void beforeLoadGame() {
        WorldConfig config = SaveManager.loadWorld();
        if (config != null) {
            //TODO: rework lifecycle for config
//            worldConfig = config;
//            SolRandom.setSeed(worldConfig.getSeed());
        }
    }

    // TODO: Make this non-static.
    public static void addResizeSubscriber(ResizeSubscriber resizeSubscriber) {
        resizeSubscribers.add(resizeSubscriber);
    }
}
