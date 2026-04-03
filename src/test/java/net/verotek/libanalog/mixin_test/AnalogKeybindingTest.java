package net.verotek.libanalog.mixin_test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.TruthJUnit.assume;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.verotek.libanalog.LibAnalog;
import net.verotek.libanalog.api.KeyMapper;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeybinding;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeyboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.voegl.analogkey4j.event.AnalogKeyState;
import org.voegl.analogkey4j.event.AnalogKeyboardListener;
import org.voegl.analogkey4j.key.HidKey;
import java.lang.reflect.Field;
import java.util.Set;

class AnalogKeybindingTest {

  private static final HidKey KEY_BINDING = HidKey.Space;

  private KeyMapping keyBinding;
  private IAnalogKeybinding analogKeybinding;
  private AnalogKeyboardListener keyboardListener;
  private static MockedStatic<Minecraft> client;

  @BeforeAll
  static void setupOnce() {
    client = mockStatic(Minecraft.class);
  }

  @BeforeEach
  void setup() throws Exception {
    Minecraft instance = mock(Minecraft.class);
    client.when(Minecraft::getInstance).thenReturn(instance);
    when(instance.isWindowActive()).thenReturn(true);
    Window window = mock(Window.class);
    doReturn(window).when(instance).getWindow();
    KeyboardHandler keyboard = spy(new KeyboardHandler(instance));
    IAnalogKeyboard analogKeyboard = (IAnalogKeyboard) keyboard;
    keyboardListener = (AnalogKeyboardListener) analogKeyboard;

    doReturn(true).when(analogKeyboard).usesAnalog();
    Field keyboardField = Minecraft.class.getDeclaredField("keyboardHandler");
    keyboardField.setAccessible(true);
    keyboardField.set(instance, (KeyboardHandler) analogKeyboard);
    //? if >=1.21.9 {
    keyBinding = new KeyMapping("Space", KeyMapper.hidToGlfw(KEY_BINDING), KeyMapping.Category.GAMEPLAY);
    //?} else {
    /*keyBinding = new KeyMapping("Space", KeyMapper.hidToGlfw(KEY_BINDING), "General");
    *///?}
    analogKeybinding = (IAnalogKeybinding) keyBinding;

    // assume we have a valid analog keyboard
    assume().that(((IAnalogKeyboard) Minecraft.getInstance().keyboardHandler).usesAnalog()).isTrue();
  }

  @AfterEach
  void cleanup() {
    if (keyboardListener != null) {
      keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, 0.0f)));
    }
  }

  @Test
  void testIsNotPressedAtStart() {
    assertThat(keyBinding.isDown()).isFalse();
  }

  @Test
  void testWasNotPressedAtStart() {
    assertThat(keyBinding.consumeClick()).isFalse();
  }

  @Test
  void testPressedAmountZeroAtStart() {
    assertThat(analogKeybinding.pressedAmount()).isZero();
  }

  @Test
  void testDoesNotAcceptWrongKey() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(HidKey.B, 1.0f)));
    assertThat(analogKeybinding.pressedAmount()).isNotEqualTo(1.0f);
  }

  @Test
  void testCorrectKeyChangesPressedAmount() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, 1.0f)));
    assertThat(analogKeybinding.pressedAmount()).isEqualTo(1.0f);
  }

  @Test
  void testCorrectKeyChangesPressed() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, 1.0f)));
    assertThat(keyBinding.isDown()).isTrue();
  }

  @Test
  void testWasPressedForCorrectKey() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, 1.0f)));
    verify(Minecraft.getInstance()).execute(any(Runnable.class));
  }

  @Test
  void testWasPressedOnlyOnceForCorrectKey() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, 1.0f)));
    verify(Minecraft.getInstance(), times(1)).execute(any(Runnable.class));
  }

  @Test
  void testSmallChangeDoesNotPressTwice() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, LibAnalog.ACTUATION_POINT)));
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, 1.0f)));
    verify(Minecraft.getInstance(), times(1)).execute(any(Runnable.class));
  }

  @Test
  void testActuationPointPressesKey() {
    keyboardListener.keyPressed(null, Set.of(new AnalogKeyState(KEY_BINDING, LibAnalog.ACTUATION_POINT)));
    verify(Minecraft.getInstance(), times(1)).execute(any(Runnable.class));
  }
}
