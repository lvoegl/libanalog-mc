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

@Implements(@Interface(iface = IAnalogKeybinding.class, prefix = "libanalog$"))
@Mixin(KeyBinding.class)
public abstract class AnalogKeybinding implements IAnalogKeybinding {

  @Shadow private boolean pressed;
  @Shadow protected InputUtil.Key boundKey;

  /**
   * @author lvoegl
   * @reason Computes isPressed based on analog value.
   */
  @Overwrite
  public boolean isPressed() {
    IAnalogKeyboard analogKeyboard = (IAnalogKeyboard) MinecraftClient.getInstance().keyboard;
    if (!analogKeyboard.usesAnalog()) {
      return pressed;
    }
    return AnalogKeyStates.isPressed(this.boundKey);
  }

  @Intrinsic
  public float libanalog$pressedAmount() {
    IAnalogKeyboard analogKeyboard = (IAnalogKeyboard) MinecraftClient.getInstance().keyboard;
    if (!analogKeyboard.usesAnalog()) {
      return pressed ? 1.0f : 0.0f;
    }
    return AnalogKeyStates.get(this.boundKey);
  }
}
