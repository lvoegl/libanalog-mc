package net.verotek.libanalog.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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

@Implements(@Interface(iface = IAnalogKeybinding.class, prefix = "libanalog$"))
@Mixin(KeyBinding.class)
public abstract class AnalogKeybinding implements IAnalogKeybinding {

  @Shadow
  private boolean pressed;
  @Shadow
  /*? if >=1.21.9 {*/ protected /*?} else {*/ /*private *//*?}*/ InputUtil.Key boundKey;

  @Unique
  private boolean isAnalog() {
    IAnalogKeyboard analogKeyboard = (IAnalogKeyboard) MinecraftClient.getInstance().keyboard;
    return analogKeyboard.usesAnalog() && this.boundKey.getCategory() == InputUtil.Type.KEYSYM;
  }

  /**
   * @author lvoegl
   * @reason Computes isPressed based on analog value.
   */
  @Overwrite
  public boolean isPressed() {
    if (!isAnalog()) {
      return pressed;
    }
    return AnalogKeyStates.isPressed(this.boundKey);
  }

  @Intrinsic
  public float libanalog$pressedAmount() {
    if (!isAnalog()) {
      return pressed ? 1.0f : 0.0f;
    }
    return AnalogKeyStates.get(this.boundKey);
  }
}
