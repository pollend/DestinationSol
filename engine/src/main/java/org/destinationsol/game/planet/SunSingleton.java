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
package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.*;

public class SunSingleton {
    public static final float SUN_HOT_RAD = .75f * Const.SUN_RADIUS;
    public static final float GRAV_CONST = 2000;
    private static final float SUN_DMG = 4f;
    private final TextureAtlas.AtlasRegion gradatingTexture;
    private final TextureAtlas.AtlasRegion whiteTexture;
    private final Color gradatingTint;
    private final Color fillTint;

    private final SolTime time;
    private final SolCam solCam;
    private final PlanetManager planetManager;

    SunSingleton(SolTime time, SolCam solCam, PlanetManager planetManager) {
        this.time = time;
        this.solCam = solCam;
        this.planetManager = planetManager;
        gradatingTexture = Assets.getAtlasRegion("engine:planetStarCommonGrad");
        whiteTexture = Assets.getAtlasRegion("engine:planetStarCommonWhiteTex");
        gradatingTint = SolColor.col(1, 1);
        fillTint = SolColor.col(1, 1);
    }

    public void draw(GameDrawer drawer) {
        Vector2 camPos = solCam.getPosition();
        SolSystem sys = planetManager.getNearestSystem(camPos);
        Vector2 toCam = SolMath.getVec(camPos);
        toCam.sub(sys.getPosition());
        float toCamLen = toCam.len();
        if (toCamLen < Const.SUN_RADIUS) {
            float closeness = 1 - toCamLen / Const.SUN_RADIUS;
            gradatingTint.a = MathUtils.clamp(closeness * 4, (float) 0, (float) 1);
            fillTint.a = MathUtils.clamp((closeness - .25f) * 4, (float) 0, (float) 1);

            float sz = 2 * solCam.getViewDistance();
            float gradAngle = SolMath.angle(toCam) + 90;
            drawer.draw(whiteTexture, sz * 2, sz * 2, sz, sz, camPos.x, camPos.y, 0, fillTint);
            drawer.draw(gradatingTexture, sz * 2, sz * 2, sz, sz, camPos.x, camPos.y, gradAngle, gradatingTint);
        }
        SolMath.free(toCam);
    }

    public void doDmg(SolObject obj, float toSys) {

        float dmg = SUN_DMG * time.getTimeStep();
        if (SUN_HOT_RAD < toSys) {
            return;
        }
        obj.receiveDmg(dmg, null, DmgType.FIRE);
    }
}
