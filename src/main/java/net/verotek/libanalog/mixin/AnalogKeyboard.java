package net.verotek.libanalog.mixin;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetKeyScancode;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import com.mojang.blaze3d.platform.Window;
//?}
//? if >=26 {
import net.minecraft.client.input.PreeditEvent;
//?}
import net.verotek.libanalog.LibAnalog;
import net.verotek.libanalog.api.AnalogKeyStates;
import net.verotek.libanalog.api.KeyMapper;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeyboard;
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
import org.voegl.analogkey4j.AnalogKeyboardManager;
import org.voegl.analogkey4j.event.AnalogKeyState;
import org.voegl.analogkey4j.event.AnalogKeyboardListener;
import org.voegl.analogkey4j.plugins.AnalogKeyboardDevice;

@Implements(@Interface(iface = IAnalogKeyboard.class, prefix = "libanalog$"))
@Mixin(KeyboardHandler.class)
public abstract class AnalogKeyboard implements AnalogKeyboardListener, IAnalogKeyboard {

  @Final @Shadow private Minecraft minecraft;
  @Unique private final AnalogKeyboardManager libanalog$manager = new AnalogKeyboardManager();
  @Unique private AnalogKeyboardDevice libanalog$keyboard;

  //? if >= 26 {
  @Shadow
  private void preeditCallback(final long handle, final PreeditEvent event) {}
  //? }

  //? if >=1.21.9 {
  @Shadow
  private void charTyped(long l, CharacterEvent characterEvent) {}

  @Shadow
  private void keyPress(long l, int i, KeyEvent keyEvent) {}
  //?} else {
  /*@Shadow
  public void keyPress(long window, int key, int scancode, int action, int modifiers) {}

  @Shadow
  private void charTyped(long window, int codePoint, int modifiers) {}
  *///?}

  @Inject(method = "<init>", at = @At("TAIL"))
  private void registerKeyboardListener(Minecraft client, CallbackInfo ci) {
    libanalog$manager.addListener(this);
    ClientLifecycleEvents.CLIENT_STOPPING.register(
        c -> {
          LibAnalog.LOGGER.info("Shutting down analog keyboard handler");
          libanalog$manager.stop();
        });
  }

  /**
   * @author lvoegl
   * @reason Sets up analog keyboard manager and falls back to default keyboard if unavailable.
   */
  @Overwrite
  public void setup(/*? if >=1.21.9 {*/ Window /*?} else {*/ /*long *//*?}*/ window) {
    LibAnalog.LOGGER.info("Initializing analog keyboard handler");
    libanalog$manager.start();

    InputConstants.setupKeyboardCallbacks(
        window,
        (windowx, key, scancode, action, modifiers) -> {
          if (!usesAnalog()) {
            // only if no analog keyboard
            //? if >=1.21.9 {
            KeyEvent keyInput = new KeyEvent(key, scancode, modifiers);
            this.minecraft.execute(() -> this.keyPress(windowx, action, keyInput));
            //?} else {
            /*this.minecraft.execute(() -> this.keyPress(windowx, key, scancode, action, modifiers));
            *///?}
          }
        },
        (windowx, codePoint /*? if <26 {*/ /*, modifiers  *//*?} */) -> {
          //? if >=1.21.9 {
          CharacterEvent characterEvent =
              new CharacterEvent(codePoint /*? if < 26 {*/ /*, modifiers  *//*?} */);
          this.minecraft.execute(() -> this.charTyped(windowx, characterEvent));
          //?} else {
          /*this.minecraft.execute(() -> this.charTyped(windowx, codePoint, modifiers));
          *///?}
        } //? if >=26 {
        ,
        (window1, preeditSize, preeditPtr, blockCount, blockSizesPtr, focusedBlock, caret) -> {
          PreeditEvent event =
              PreeditEvent.createFromCallback(
                  preeditSize, preeditPtr, blockCount, blockSizesPtr, focusedBlock, caret);
          this.minecraft.execute(() -> this.preeditCallback(window1, event));
        },
        (window1) -> this.minecraft.textInputManager().notifyIMEChanged());
        //? } else {
        /*);
        *///? }
  }

  @Intrinsic
  public boolean libanalog$usesAnalog() {
    return libanalog$keyboard != null && !libanalog$keyboard.isClosed();
  }

  @Override
  public void keyPressed(AnalogKeyboardDevice keyboard, Set<AnalogKeyState> states) {
    if (!Minecraft.getInstance().isWindowActive()) return;

    for (AnalogKeyState state : states) {
      int keyCode = KeyMapper.hidToGlfw(state.key());
      //? if >=1.21.9 {
      long handle = this.minecraft.getWindow().handle();
      KeyEvent keyInput = new KeyEvent(keyCode, glfwGetKeyScancode(keyCode), 0); // TODO modifiers
      InputConstants.Key key = InputConstants.getKey(keyInput);
      //?} else {
      /*long handle = this.minecraft.getWindow().getWindow();
      int scancode = glfwGetKeyScancode(keyCode);
      InputConstants.Key key = InputConstants.getKey(keyCode, scancode);
      *///?}
      boolean wasPressed = AnalogKeyStates.isPressed(key);
      AnalogKeyStates.set(key, state.value());
      boolean isPressed = AnalogKeyStates.isPressed(key);

      if (!wasPressed && isPressed) {
        //? if >=1.21.9 {
        this.minecraft.execute(() -> this.keyPress(handle, GLFW_PRESS, keyInput));
        //?} else {
        /*this.minecraft.execute(() -> this.keyPress(handle, keyCode, scancode, GLFW_PRESS, 0));
        *///?}
      } else if (wasPressed && !isPressed) {
        //? if >=1.21.9 {
        this.minecraft.execute(() -> this.keyPress(handle, GLFW_RELEASE, keyInput));
        //?} else {
        /*this.minecraft.execute(() -> this.keyPress(handle, keyCode, scancode, GLFW_RELEASE, 0));
        *///?}
      }
    }
  }

  @Unique
  private static String formatVidPid(int vid, int pid) {
    return String.format("%04x:%04x", vid, pid);
  }

  @Override
  public void keyboardAdded(AnalogKeyboardDevice keyboard) {
    // TODO do not always use the first supported keyboard found
    if (this.libanalog$keyboard == null) {
      LibAnalog.LOGGER.info(
          "Keyboard {} connected", formatVidPid(keyboard.getVendorId(), keyboard.getProductId()));
      this.libanalog$keyboard = keyboard;
      keyboard.open();
    }
  }

  @Override
  public void keyboardClosed(AnalogKeyboardDevice keyboard) {}

  @Override
  public void keyboardError(AnalogKeyboardDevice keyboard, String message) {
    LibAnalog.LOGGER.error(message);
  }

  @Override
  public void keyboardOpened(AnalogKeyboardDevice keyboard) {}

  @Override
  public void keyboardRemoved(AnalogKeyboardDevice keyboard) {
    if (keyboard.equals(this.libanalog$keyboard)) {
      LibAnalog.LOGGER.info(
          "Keyboard {} disconnected",
          formatVidPid(keyboard.getVendorId(), keyboard.getProductId()));
      this.libanalog$keyboard = null;
    }
  }
}
