package net.verotek.libanalog.mixin;

import java.util.Map;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.verotek.libanalog.LibAnalog;
import net.verotek.libanalog.api.AnalogEventHandler;
import net.verotek.libanalog.api.KeyMapper;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeybinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Implements(@Interface(iface = IAnalogKeybinding.class, prefix = "libanalog$"))
@Mixin(KeyBinding.class)
public abstract class AnalogKeybinding implements IAnalogKeybinding {

  @Shadow private InputUtil.Key boundKey;
  @Shadow private boolean pressed;
  @Shadow @Final private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;

  @Shadow
  public abstract void setPressed(boolean pressed);

  @Shadow private int timesPressed;
  @Unique private float pressedAmount = 0.0f;
  @Unique private float minPressedAmountSincePress = 1.0f;
  @Unique private boolean shadowPressed = false;
  @Unique private boolean keySupported = false;
  @Unique private static final AnalogEventHandler HANDLER = AnalogEventHandler.getInstance();

  @Unique private void checkSupported() {
    keySupported = KeyMapper.glfwToHid(boundKey.getCode()) != null;
  }

  @Unique private boolean useAnalog() {
    return HANDLER.canUseAnalog();
  }

  @Inject(method = "<init>*", at = @At("RETURN"))
  private void afterInit(CallbackInfo ci) {
    checkSupported();
  }

  @Inject(method = "setBoundKey", at = @At("RETURN"))
  private void afterSetKeycode(CallbackInfo ci) {
    checkSupported();
  }

  @Inject(method = "reset", at = @At("RETURN"))
  private void reset(CallbackInfo ci) {
    pressedAmount = 0.0f;
  }

  /**
   * @author lvoegl
   * @reason Provides compatability with non-analog keyboards without interfering with analog
   *     events.
   */
  @Overwrite
  public static void onKeyPressed(InputUtil.Key key) {
    KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);
    if (keyBinding != null) {
      IAnalogKeybinding analogKeybinding = (IAnalogKeybinding) keyBinding;
      if (!analogKeybinding.analogActive()) {
        analogKeybinding.incrementTimesPressed();
      }
    }
  }

  /**
   * @author lvoegl
   * @reason Provides compatability with non-analog keyboards without interfering with analog
   *     events.
   */
  @Overwrite
  public static void setKeyPressed(InputUtil.Key key, boolean pressed) {
    KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);
    if (keyBinding != null) {
      IAnalogKeybinding analogKeybinding = (IAnalogKeybinding) keyBinding;
      if (!analogKeybinding.analogActive()) {
        keyBinding.setPressed(pressed);
      }
    }
  }

  @Unique private void setBothPressed(boolean pressed) {
    shadowPressed = pressed;
    setPressed(pressed);
  }

  @Intrinsic
  public synchronized void libanalog$processAnalogEvent(int keyCode, float pressedAmount) {
    if (keyCode != boundKey.getCode()) return;

    this.pressedAmount = pressedAmount;

    if (pressedAmount >= LibAnalog.ACTUATION_POINT) {
      if (pressedAmount - minPressedAmountSincePress >= LibAnalog.MIN_ACTUATION_DELTA) {
        if (!shadowPressed) {
          setBothPressed(true);
          incrementTimesPressed();
          minPressedAmountSincePress = pressedAmount;
        }
      }
    } else {
      setBothPressed(false);
      if (pressedAmount < minPressedAmountSincePress) {
        minPressedAmountSincePress = pressedAmount;
      }
    }
  }

  @Intrinsic
  public float libanalog$pressedAmount() {
    if (!analogActive()) {
      return pressed ? 1.0f : 0.0f;
    }
    return pressedAmount;
  }

  @Intrinsic
  public void libanalog$incrementTimesPressed() {
    timesPressed++;
  }

  @Intrinsic
  public boolean libanalog$analogActive() {
    return useAnalog() && keySupported;
  }
}
