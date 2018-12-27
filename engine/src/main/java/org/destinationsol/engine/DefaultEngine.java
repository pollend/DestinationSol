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
package org.destinationsol.engine;

import org.destinationsol.GameOptions;
import org.destinationsol.ModuleManager;
import org.destinationsol.SolFileReader;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggMusicFileFormat;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundFileFormat;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.emitters.EmitterFileFormat;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.fonts.FontFileFormat;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.JsonFileFormat;
import org.destinationsol.assets.textures.DSTexture;
import org.destinationsol.assets.textures.DSTextureFileFormat;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.rendering.CanvasRenderer;
import org.destinationsol.rendering.LibGdxCanvas;
import org.destinationsol.ui.SolInputManager;
import org.terasology.assets.format.producer.AssetFileDataProducer;
import org.terasology.assets.management.AssetTypeManager;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.entitysystem.component.CodeGenComponentManager;
import org.terasology.entitysystem.component.ComponentManager;
import org.terasology.entitysystem.core.EntityManager;
import org.terasology.entitysystem.entity.inmemory.InMemoryEntityManager;
import org.terasology.entitysystem.event.impl.EventProcessor;
import org.terasology.entitysystem.event.impl.EventProcessorBuilder;
import org.terasology.entitysystem.transaction.TransactionManager;
import org.terasology.valuetype.TypeLibrary;

public class DefaultEngine implements EngineFactory {

    private final TransactionManager transactionManager;
    private final ComponentManager componentManager;
    private final EventProcessor eventProcessor;
    private final OggSoundManager oggSoundManager;
    private final OggMusicManager oggMusicManager;
    private final SolInputManager inputManager;
    private final boolean isMobile;
    private final SolFileReader fileReader;
    private final Context context;
    private final EntityManager entityManager;
    private final CanvasRenderer canvas;
    private final GameOptions gameOptions;

    public DefaultEngine(boolean isMobile, SolFileReader solFileReader){
        this.fileReader = solFileReader;
        this.isMobile = isMobile;
        this.componentManager = new CodeGenComponentManager(new TypeLibrary());
        this.transactionManager = new TransactionManager();
        this.eventProcessor =   new EventProcessorBuilder().build();
        this.oggSoundManager = new OggSoundManager();
        this.oggMusicManager = new OggMusicManager();
        this.inputManager = new SolInputManager(oggSoundManager);
        this.entityManager = new InMemoryEntityManager(componentManager,transactionManager);
        this.canvas = new LibGdxCanvas();
        this.gameOptions = new GameOptions(isMobile,solFileReader);

        // configure context
        this.context = new ContextImpl();
        context.put(SolInputManager.class,inputManager);
        context.put(OggSoundManager.class,oggSoundManager);
        context.put(OggMusicManager.class,oggMusicManager);
        context.put(EntityManager.class,entityManager);
        context.put(CanvasRenderer.class,canvas);
        context.put(GameOptions.class,gameOptions);

    }

    @Override
    public CanvasRenderer canvas() {
        return canvas;
    }

    @Override
    public EntityManager entityManager() {
        return entityManager;
    }

    @Override
    public AssetTypeManager assetTypeManager() {
        ModuleAwareAssetTypeManager assetTypeManager = new ModuleAwareAssetTypeManager();
        ((AssetFileDataProducer)assetTypeManager.createAssetType(OggSound.class, OggSound::new, "sounds").getProducers().get(0)).addAssetFormat(new OggSoundFileFormat());
        ((AssetFileDataProducer)assetTypeManager.createAssetType(OggMusic.class, OggMusic::new, "music").getProducers().get(0)).addAssetFormat(new OggMusicFileFormat());
        ((AssetFileDataProducer)assetTypeManager.createAssetType(Font.class, Font::new, "fonts").getProducers().get(0)).addAssetFormat(new FontFileFormat());
        ((AssetFileDataProducer)assetTypeManager.createAssetType(Emitter.class, Emitter::new, "emitters").getProducers().get(0)).addAssetFormat(new EmitterFileFormat());
        ((AssetFileDataProducer)assetTypeManager.createAssetType(Json.class, Json::new, "collisionMeshes", "ships", "items", "configs", "grounds", "mazes", "asteroids").getProducers().get(0)).addAssetFormat(new JsonFileFormat());
        ((AssetFileDataProducer)assetTypeManager.createAssetType(DSTexture.class, DSTexture::new, "textures", "ships", "items", "grounds", "mazes", "asteroids").getProducers().get(0)).addAssetFormat(new DSTextureFileFormat());
        return assetTypeManager;
    }

    @Override
    public Context context() {
        return context;
    }


    @Override
    public OggMusicManager musicManager() {
        return this.oggMusicManager;
    }

    @Override
    public OggSoundManager soundManager() {
        return this.oggSoundManager;
    }

    @Override
    public SolInputManager inputManager() {
        return this.inputManager;
    }


    @Override
    public GameOptions options() {
        return gameOptions;
    }

}
