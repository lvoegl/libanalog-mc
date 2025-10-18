package net.verotek.libanalog.mixin;

import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
//? if >=1.21.9 {
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.Window;
//?}
import net.minecraft.client.util.InputUtil;
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
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import static org.lwjgl.glfw.GLFW.glfwGetKeyScancode;

@Implements(@Interface(iface = IAnalogKeyboard.class, prefix = "libanalog$"))
@Mixin(Keyboard.class)
public abstract class AnalogKeyboard implements AnalogKeyboardListener, IAnalogKeyboard {

  @Final
  @Shadow
  private MinecraftClient client;
  @Unique
  private final AnalogKeyboardManager libanalog$manager = new AnalogKeyboardManager();
  @Unique
  private AnalogKeyboardDevice libanalog$keyboard;

  //? if >=1.21.9 {
  @Shadow
  private void onKey(long window, int action, KeyInput input) {
  }

  @Shadow
  private void onChar(long window, CharInput input) {
  }
  //?} else {
  /*@Shadow
  public void onKey(long window, int key, int scancode, int action, int modifiers) {
  }

  @Shadow
  private void onChar(long window, int codePoint, int modifiers) {
  }
  *///?}

  @Inject(method = "<init>", at = @At("TAIL"))
  private void registerKeyboardListener(MinecraftClient client, CallbackInfo ci) {
    libanalog$manager.addListener(this);
    ClientLifecycleEvents.CLIENT_STOPPING.register(c -> {
      LibAnalog.LOGGER.info("Shutting down analog keyboard handler");
      libanalog$manager.stop();
    });
  }

  /**
   * @author lvoegl
   * @reason Sets up analog keyboard manager and falls back to default keyboard if
   *         unavailable.
   */
  @Overwrite
  public void setup(/*? if >=1.21.9 {*/ Window /*?} else {*/ /*long*//*?}*/ window) {
    LibAnalog.LOGGER.info("Initializing analog keyboard handler");
    libanalog$manager.start();

    InputUtil.setKeyboardCallbacks(window, (windowx, key, scancode, action, modifiers) -> {
      if (!usesAnalog()) {
        // only if no analog keyboard
        //? if >=1.21.9 {
        KeyInput keyInput = new KeyInput(key, scancode, modifiers);
        this.client.execute(() -> this.onKey(windowx, action, keyInput));
        //?} else {
        /*this.client.execute(() -> this.onKey(windowx, key, scancode, action, modifiers));
        *///?}
      }
    }, (windowx, codePoint, modifiers) -> {
      //? if >=1.21.9 {
      CharInput charInput = new CharInput(codePoint, modifiers);
      this.client.execute(() -> this.onChar(windowx, charInput));
      //?} else {
      /*this.client.execute(() -> this.onChar(windowx, codePoint, modifiers));
      *///?}
    });
  }

  @Intrinsic
  public boolean libanalog$usesAnalog() {
    return libanalog$keyboard != null && !libanalog$keyboard.isClosed();
  }

  @Override
  public void keyPressed(AnalogKeyboardDevice keyboard, Set<AnalogKeyState> states) {
    if (!MinecraftClient.getInstance().isWindowFocused()) return;

    for (AnalogKeyState state : states) {
      int keyCode = KeyMapper.hidToGlfw(state.key());
      long handle = this.client.getWindow().getHandle();
      //? if >=1.21.9 {
      KeyInput keyInput = new KeyInput(keyCode, glfwGetKeyScancode(keyCode), 0); // TODO modifiers
      InputUtil.Key key = InputUtil.fromKeyCode(keyInput);
      //?} else {
      /*int scancode = glfwGetKeyScancode(keyCode);
      InputUtil.Key key = InputUtil.fromKeyCode(keyCode, scancode);
      *///?}
      boolean wasPressed = AnalogKeyStates.isPressed(key);
      AnalogKeyStates.set(key, state.value());
      boolean isPressed = AnalogKeyStates.isPressed(key);

      if (!wasPressed && isPressed) {
        //? if >=1.21.9 {
        this.client.execute(() -> this.onKey(handle, GLFW_PRESS, keyInput));
        //?} else {
        /*this.client.execute(() -> this.onKey(handle, keyCode, scancode, GLFW_PRESS, 0));
        *///?}
      } else if (wasPressed && !isPressed) {
        //? if >=1.21.9 {
        this.client.execute(() -> this.onKey(handle, GLFW_RELEASE, keyInput));
        //?} else {
        /*this.client.execute(() -> this.onKey(handle, keyCode, scancode, GLFW_RELEASE, 0));
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
      LibAnalog.LOGGER.info("Keyboard {} connected", formatVidPid(keyboard.getVendorId(), keyboard.getProductId()));
      this.libanalog$keyboard = keyboard;
      keyboard.open();
    }
  }

  @Override
  public void keyboardClosed(AnalogKeyboardDevice keyboard) {
  }

  @Override
  public void keyboardError(AnalogKeyboardDevice keyboard, String message) {
    LibAnalog.LOGGER.error(message);
  }

  @Override
  public void keyboardOpened(AnalogKeyboardDevice keyboard) {
  }

  @Override
  public void keyboardRemoved(AnalogKeyboardDevice keyboard) {
    if (keyboard.equals(this.libanalog$keyboard)) {
      LibAnalog.LOGGER.info("Keyboard {} disconnected", formatVidPid(keyboard.getVendorId(), keyboard.getProductId()));
      this.libanalog$keyboard = null;
    }
  }
}
