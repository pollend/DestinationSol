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

package org.destinationsol.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.AbilityCommonConfig;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolTime;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.planet.Planet;

public class Teleport implements ShipAbility {
    private static final int MAX_RADIUS = 4;
    private final Vector2 newPos;
    private final Config config;
    private boolean shouldTeleport;
    private float angle;

    private final PartMan partMan;

    public Teleport(Config config, PartMan partMan) {
        this.config = config;
        newPos = new Vector2();
        this.partMan= partMan;
    }

    @Override
    public boolean update(SolTime solTime, SolShip owner, boolean tryToUse) {
        shouldTeleport = false;
        if (!tryToUse) {
            return false;
        }
        Vector2 position = owner.getPosition();
        Faction faction = owner.getPilot().getFaction();
        SolShip ne = game.getFactionMan().getNearestEnemy(game, MAX_RADIUS, faction, position);
        if (ne == null) {
            return false;
        }
        Vector2 nePos = ne.getPosition();
        Planet np = game.getPlanetManager().getNearestPlanet();
        if (np.isNearGround(nePos)) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            newPos.set(position);
            newPos.sub(nePos);
            angle = config.angle * SolRandom.randomFloat(.5f, 1) * SolMath.toInt(SolRandom.test(.5f));
            SolMath.rotate(newPos, angle);
            newPos.add(nePos);
            if (game.isPlaceEmpty(newPos, false)) {
                shouldTeleport = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public AbilityConfig getConfig() {
        return config;
    }

    @Override
    public AbilityCommonConfig getCommonConfig() {
        return config.cc;
    }

    @Override
    public float getRadius() {
        return MAX_RADIUS;
    }

    // can be performed in update
    public void maybeTeleport(SolShip owner) {
        if (!shouldTeleport) {
            return;
        }

        TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion("engine:teleportBlip");
        float blipSz = owner.getHull().config.getApproxRadius() * 3;
        partMan.blip(owner.getPosition(), SolRandom.randomFloat(180), blipSz, 1, Vector2.Zero, tex);
        partMan.blip(newPos, SolRandom.randomFloat(180), blipSz, 1, Vector2.Zero, tex);

        float newAngle = owner.getAngle() + angle;
        Vector2 newSpeed = SolMath.getVec(owner.getSpeed());
        SolMath.rotate(newSpeed, angle);

        Body body = owner.getHull().getBody();
        body.setTransform(newPos, newAngle * MathUtils.degRad);
        body.setLinearVelocity(newSpeed);

        SolMath.free(newSpeed);
    }

    public static class Config implements AbilityConfig {
        private final float angle;
        private final SolItem chargeExample;
        private final float rechargeTime;
        private final AbilityCommonConfig cc;
        private final PartMan partMan;

        public Config(float angle, SolItem chargeExample, float rechargeTime, AbilityCommonConfig cc,PartMan partMan) {
            this.angle = angle;
            this.chargeExample = chargeExample;
            this.rechargeTime = rechargeTime;
            this.cc = cc;
            this.partMan = partMan;
        }

        public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc,PartMan partMan) {
            float angle = abNode.getFloat("angle");
            SolItem chargeExample = itemManager.getExample("teleportCharge");
            float rechargeTime = abNode.getFloat("rechargeTime");
            return new Config(angle, chargeExample, rechargeTime, cc,partMan);
        }

        public ShipAbility build() {
            return new Teleport(this,partMan);
        }

        @Override
        public SolItem getChargeExample() {
            return chargeExample;
        }

        @Override
        public float getRechargeTime() {
            return rechargeTime;
        }

        @Override
        public void appendDesc(StringBuilder sb) {
            sb.append("Teleport around enemy");
        }
    }
}
