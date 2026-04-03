package net.verotek.libanalog.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeybinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class AnalogUnlockOnScreenClose {

  @Final @Shadow public Options options;

  @Inject(method = "setScreen", at = @At("TAIL"))
  private void unlockAnalogKeysOnScreenClose(Screen screen, CallbackInfo ci) {
    if (screen != null) return;

    for (KeyMapping km : this.options.keyMappings) {
      if (km instanceof IAnalogKeybinding ak) {
        ak.setLocked(false);
      }
    }
  }
}
