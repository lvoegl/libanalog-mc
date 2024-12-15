package net.verotek.libanalog.api;

import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.verotek.libanalog.LibAnalog;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeybinding;
import org.voegl.analogkey4j.AnalogKeyboardManager;
import org.voegl.analogkey4j.event.AnalogKeyState;
import org.voegl.analogkey4j.event.AnalogKeyboardListener;
import org.voegl.analogkey4j.plugins.AnalogKeyboardDevice;

public class AnalogEventHandler implements AnalogKeyboardListener {

  private final MinecraftClient MINECRAFT_CLIENT = MinecraftClient.getInstance();
  private final AnalogKeyboardManager manager = new AnalogKeyboardManager();
  private AnalogKeyboardDevice keyboard;
  private static final AnalogEventHandler INSTANCE = new AnalogEventHandler();

  private AnalogEventHandler() {
    manager.addListener(this);
  }

  public static AnalogEventHandler getInstance() {
    return INSTANCE;
  }

  public boolean canUseAnalog() {
    return keyboard != null;
  }

  public void start() {
    manager.start();
  }

  public void stop() {
    manager.stop();
  }

  @Override
  public void keyPressed(AnalogKeyboardDevice keyboard, Set<AnalogKeyState> states) {
    // TODO this sometimes does not work when screens render longer than expected
    if (MINECRAFT_CLIENT.currentScreen != null) return;

    for (AnalogKeyState state : states) {
      int keyCode = KeyMapper.hidToGlfw(state.key());
      float pressedAmount = state.value();
      for (KeyBinding keyBinding : MINECRAFT_CLIENT.options.allKeys) {
        IAnalogKeybinding analogKeybinding = (IAnalogKeybinding) keyBinding;
        analogKeybinding.processAnalogEvent(keyCode, pressedAmount);
      }
    }
  }

  private static String formatVidPid(int vid, int pid) {
    return String.format("%04x:%04x", vid, pid);
  }

  @Override
  public void keyboardAdded(AnalogKeyboardDevice keyboard) {
    // TODO do not always use the first supported keyboard found
    if (this.keyboard == null) {
      LibAnalog.LOGGER.info("Keyboard {} connected", formatVidPid(keyboard.getVendorId(), keyboard.getProductId()));
      this.keyboard = keyboard;
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
    if (keyboard.equals(this.keyboard)) {
      LibAnalog.LOGGER.info("Keyboard {} disconnected", formatVidPid(keyboard.getVendorId(), keyboard.getProductId()));
      this.keyboard = null;
    }
  }
}
