package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.planet.Planet;

public class ParticleSrc implements Dra {
  public static final float JUMP_SPD_TRESH = 1f;
  public static final float MAX_TIME_BETWEEN_POS_CHANGE = .25f;
  private static final float JUMP_SZ_THRESH = .7f;
  private final ParticleEmitter myEmitter;
  private final ParticleEmitter.ScaledNumericValue myOrigSpdAngle;
  private final ParticleEmitter.ScaledNumericValue myOrigRot;
  private final DraLevel myDraLevel;
  private final Vector2 myRelPos;
  private final Vector2 myOrigRelPos;
  private final float myAreaSz;
  private final EffectConfig myConfig;
  private final boolean myInheritsSpd;

  private Vector2 myPos;
  private boolean myWorking;
  private float myTimeSincePosChange;
  private boolean myFloatedUp;

  public ParticleSrc(EffectConfig config, float sz, DraLevel draLevel, Vector2 relPos, boolean inheritsSpd,
    SolGame game, Vector2 basePos, Vector2 baseSpd)
  {
    myConfig = config;
    myEmitter = myConfig.effectType.newEmitter();
    myDraLevel = draLevel;
    myRelPos = new Vector2(relPos);
    myOrigRelPos = new Vector2(relPos);
    myPos = new Vector2();

    if (sz <= 0) sz = config.sz;

    boolean hasArea = myEmitter.getSpawnShape().getShape() != ParticleEmitter.SpawnShape.point;
    boolean movesFast = JUMP_SPD_TRESH < myEmitter.getVelocity().getHighMax();
    boolean bigScale = JUMP_SZ_THRESH < myEmitter.getScale().getHighMax();

    if (hasArea) {
      mulVal(myEmitter.getEmission(), sz * sz);
      mulVal(myEmitter.getSpawnWidth(), sz);
      mulVal(myEmitter.getSpawnHeight(), sz);
      myAreaSz = 0;
    } else if (movesFast) {
      mulVal(myEmitter.getEmission(), sz * sz);
      ParticleEmitter.ScaledNumericValue vel = myEmitter.getVelocity();
      vel.setHigh(vel.getHighMin() * sz, vel.getHighMax() * sz);
      myAreaSz = 0;
    } else if (bigScale) {
      ParticleEmitter.ScaledNumericValue scale = myEmitter.getScale();
      scale.setHigh(scale.getHighMin() * sz, scale.getHighMax() * sz);
      myAreaSz = 0;
    } else {
      myAreaSz = sz;
    }
    myEmitter.setSprite(new Sprite(myConfig.tex.getTexture()));
    float[] tint = myEmitter.getTint().getColors();
    tint[0] = config.tint.r;
    tint[1] = config.tint.g;
    tint[2] = config.tint.b;

    myOrigSpdAngle = new ParticleEmitter.ScaledNumericValue();
    transferAngle(myEmitter.getAngle(), myOrigSpdAngle, 0f);
    myOrigRot = new ParticleEmitter.ScaledNumericValue();
    transferAngle(myEmitter.getRotation(), myOrigRot, 0f);

    myInheritsSpd = inheritsSpd;
    updateSpd(game, baseSpd, basePos);

    if (myConfig.effectType.continuous) {
      // making it continuous after setting initial speed
      myEmitter.setContinuous(true);
      // this is needed because making effect continuous starts it
      myEmitter.allowCompletion();
      // ... and still initial speed is not applied. : (
    } else {
      myEmitter.start();
    }
  }

  private void setVal(ParticleEmitter.ScaledNumericValue val, float v) {
    val.setHigh(v, v);
    val.setLow(v, v);
  }

  private void mulVal(ParticleEmitter.ScaledNumericValue val, float mul) {
    val.setHigh(val.getHighMin() * mul, val.getHighMax() * mul);
    val.setLow(val.getLowMin() * mul, val.getLowMax() * mul);
  }

  private static void transferAngle(ParticleEmitter.ScaledNumericValue from, ParticleEmitter.ScaledNumericValue to, float diff) {
    if (!to.isRelative()) to.setHigh(from.getHighMin() + diff, from.getHighMax() + diff);
    to.setLow(from.getLowMin() + diff, from.getLowMax() + diff);
  }

  public void setWorking(boolean working) {
    if (!isContinuous()) throw new AssertionError("only continuous emitters can start working");
    if (myWorking == working) return;
    myWorking = working;
    if (myWorking) myEmitter.start();
    else myEmitter.allowCompletion();
  }

  public boolean isComplete() {
    return myEmitter.isComplete();
  }

  public void update(SolGame game, SolObj o) {
    maybeSwitchRelPos(game);
    Vector2 basePos = o.getPos();
    float baseAngle = o.getAngle();
    SolMath.toWorld(myPos, myRelPos, baseAngle, basePos, false);
    float ts = game.getTimeStep();
    fixSpeedBug(ts);
    myEmitter.setPosition(myPos.x, myPos.y);
    setAngle(baseAngle);
    updateSpd(game, o.getSpd(), o.getPos());
    myEmitter.update(ts);
  }

  private void updateSpd(SolGame game, Vector2 baseSpd, Vector2 basePos) {
    if (isContinuous()) {
      if (!isWorking()) return;
    } else {
      if (myFloatedUp) return;
      myFloatedUp = true;
    }
    if (!myInheritsSpd) baseSpd = Vector2.Zero;
    if (!myConfig.floatsUp) {
      setSpd(baseSpd);
      return;
    }
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 spd = np.getAdjustedEffectSpd(basePos, baseSpd);
    setSpd(spd);
    SolMath.free(spd);
  }

  private void maybeSwitchRelPos(SolGame game) {
    if (myAreaSz == 0) return;
    float ts = game.getTimeStep();
    myTimeSincePosChange += ts;
    if (!myWorking || myTimeSincePosChange < MAX_TIME_BETWEEN_POS_CHANGE) return;
    myTimeSincePosChange = 0;
    SolMath.fromAl(myRelPos, SolMath.rnd(180), SolMath.rnd(0, myAreaSz));
    myRelPos.add(myOrigRelPos);
  }

  private void fixSpeedBug(float ts) {
    myPos.x -= myEmitter.getWind().getLowMin() * ts;
    myPos.y -= myEmitter.getGravity().getLowMin() * ts;
  }

  private void setAngle(float angle) {
    transferAngle(myOrigSpdAngle, myEmitter.getAngle(), angle);
    boolean includeSpriteAngle = true;
    if (includeSpriteAngle) {
      transferAngle(myOrigRot, myEmitter.getRotation(), angle);
    }
  }

  private void setSpd(Vector2 spd) {
    ParticleEmitter.ScaledNumericValue w = myEmitter.getWind();
    w.setActive(true);
    w.setHigh(spd.x);
    w.setLow(spd.x);
    ParticleEmitter.ScaledNumericValue g = myEmitter.getGravity();
    g.setActive(true);
    g.setHigh(spd.y);
    g.setLow(spd.y);
  }

  @Override
  public void prepare(SolObj o) {
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public Vector2 getRelPos() {
    return myRelPos;
  }

  @Override
  public float getRadius() {
//    Vector3 c = myBb.getCenter();
//    float toCenter = myPos.dst(c.x, c.y);
//    float radius = myBb.getDimensions().len() / 2;
//    return toCenter + radius;
    return 1;
  }

  @Override
  public void draw(Drawer drawer, SolGame game) {
    drawer.draw(myEmitter);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean okToRemove() {
    return isComplete();
  }

  @Override
  public DraLevel getLevel() {
    return myDraLevel;
  }

  @Override
  public Texture getTex0() {
    return myConfig.tex.getTexture();
  }

  @Override
  public TextureAtlas.AtlasRegion getTex() {
    return myConfig.tex;
  }

  public boolean isContinuous() {
    return myConfig.effectType.continuous;
  }

  public boolean isWorking() {
    return myWorking;
  }
}
