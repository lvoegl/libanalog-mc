package net.verotek.libanalog.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.verotek.libanalog.api.AnalogKeyStates;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeybinding;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeyboard;
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
@Mixin(KeyMapping.class)
public abstract class AnalogKeybinding implements IAnalogKeybinding {

  @Shadow private boolean isDown;
  @Shadow /*? if >=1.21.9 {*/ protected /*?} else {*/ /*private  *//*?}*/ InputConstants.Key key;

  @Unique private boolean locked = false;

  @Unique
  private boolean isAnalog() {
    IAnalogKeyboard analogKeyboard = (IAnalogKeyboard) Minecraft.getInstance().keyboardHandler;
    return analogKeyboard.usesAnalog() && this.key.getType() == InputConstants.Type.KEYSYM;
  }

  @Inject(method = "release", at = @At("TAIL"))
  protected void release(CallbackInfo ci) {
    locked = true;
  }

  /**
   * @author lvoegl
   * @reason Computes isPressed based on analog value.
   */
  @Overwrite
  public boolean isDown() {
    if (!isAnalog()) {
      return isDown;
    }
    boolean pressed = AnalogKeyStates.isPressed(this.key);

    if (locked) {
      // if release latch is set, don't press until fully released
      if (!pressed) {
        // release if not pressed anymore
        locked = false;
      }
      return false;
    }

    return pressed;
  }

  @Intrinsic
  public float libanalog$pressedAmount() {
    if (!isAnalog()) {
      return isDown ? 1.0f : 0.0f;
    }
    return AnalogKeyStates.get(this.key);
  }

  @Intrinsic
  public void libanalog$setLocked(boolean locked) {
    this.locked = locked;
  }
}
