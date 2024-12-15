package net.verotek.libanalog.mixin_test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.TruthJUnit.assume;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import net.minecraft.client.option.KeyBinding;
import net.verotek.libanalog.LibAnalog;
import net.verotek.libanalog.api.AnalogEventHandler;
import net.verotek.libanalog.api.KeyMapper;
import net.verotek.libanalog.interfaces.mixin.IAnalogKeybinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.voegl.analogkey4j.key.HidKey;
import org.voegl.analogkey4j.plugins.AnalogKeyboardDevice;

class AnalogKeybindingTest {

  private static final HidKey KEY_BINDING = HidKey.Space;
  private KeyBinding keyBinding;
  private IAnalogKeybinding analogKeybinding;

  @BeforeEach
  void setup() {
    KeyBinding keyBinding = new KeyBinding("Space", KeyMapper.hidToGlfw(KEY_BINDING), "General");
    IAnalogKeybinding analogKeyBinding = (IAnalogKeybinding) keyBinding;

    AnalogEventHandler.getInstance().keyboardAdded(mock(AnalogKeyboardDevice.class));
    // assume we have a valid analog key binding
    assume().that(analogKeyBinding.analogActive()).isTrue();

    this.keyBinding = keyBinding;
    this.analogKeybinding = analogKeyBinding;
  }

  @Test
  void testIsNotPressedAtStart() {
    assertThat(keyBinding.isPressed()).isFalse();
  }

  @Test
  void testWasNotPressedAtStart() {
    assertThat(keyBinding.wasPressed()).isFalse();
  }

  @Test
  void testPressedAmountZeroAtStart() {
    assertThat(analogKeybinding.pressedAmount()).isZero();
  }

  @Test
  void testPressureAmountResets() throws Exception {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 1.0f);
    assume().that(analogKeybinding.pressedAmount()).isNonZero();

    Method method = KeyBinding.class.getDeclaredMethod("reset");
    method.setAccessible(true);
    method.invoke(analogKeybinding);

    assertThat(analogKeybinding.pressedAmount()).isZero();
  }

  @Test
  void testDoesNotAcceptWrongKey() {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(HidKey.B), 1.0f);

    assertThat(analogKeybinding.pressedAmount()).isNotEqualTo(1.0f);
  }

  @Test
  void testCorrectKeyChangesPressedAmount() {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 1.0f);

    assertThat(analogKeybinding.pressedAmount()).isEqualTo(1.0f);
  }

  @Test
  void testCorrectKeyChangesPressed() {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 1.0f);

    assertThat(keyBinding.isPressed()).isTrue();
  }

  @Test
  void testWasPressedForCorrectKey() {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 1.0f);

    assertThat(keyBinding.wasPressed()).isTrue();
  }

  @Test
  void testWasPressedOnlyOnceForCorrectKey() {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 1.0f);

    assume().that(keyBinding.wasPressed()).isTrue();

    assertThat(keyBinding.wasPressed()).isFalse();
  }

  @Test
  void testSmallChangeDoesNotPressTwice() {
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 0.9f);
    analogKeybinding.processAnalogEvent(KeyMapper.hidToGlfw(KEY_BINDING), 1.0f);

    assume().that(keyBinding.wasPressed()).isTrue();

    assertThat(keyBinding.wasPressed()).isFalse();
  }

  @Test
  void testActuationPointPressesKey() {
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);

    assertThat(keyBinding.isPressed()).isTrue();
  }

  @Test
  void testUnderActuationPointDisablesKey() {
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);
    assume().that(keyBinding.isPressed()).isTrue();

    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT - 0.01f);

    assertThat(keyBinding.isPressed()).isFalse();
  }

  @Test
  void testSmallLowerChangeAtActuationPointDoesNotPress() {
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING),
        LibAnalog.ACTUATION_POINT - LibAnalog.MIN_ACTUATION_DELTA + 0.0001f);
    assume().that(keyBinding.isPressed()).isFalse();

    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);

    assertThat(keyBinding.isPressed()).isFalse();
  }

  @Test
  void testSmallUpperChangeAtActuationPointDoesNotPress() {
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING),
        LibAnalog.ACTUATION_POINT + LibAnalog.MIN_ACTUATION_DELTA - 0.0001f);
    assume().that(keyBinding.isPressed()).isTrue();

    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING),
        LibAnalog.ACTUATION_POINT - LibAnalog.MIN_ACTUATION_DELTA);

    assertThat(keyBinding.isPressed()).isFalse();
  }

  @Test
  void testLowerActuationDeltaPressesKey() {
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING),
        LibAnalog.ACTUATION_POINT - LibAnalog.MIN_ACTUATION_DELTA + 0.0001f);
    assume().that(keyBinding.isPressed()).isFalse();

    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING),
        LibAnalog.ACTUATION_POINT - LibAnalog.MIN_ACTUATION_DELTA);
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);
  }

  @Test
  void testUpperActuationDeltaPressesKey() {
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT);
    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING), LibAnalog.ACTUATION_POINT - 0.0001f);
    assume().that(keyBinding.isPressed()).isFalse();

    analogKeybinding.processAnalogEvent(
        KeyMapper.hidToGlfw(KEY_BINDING),
        LibAnalog.ACTUATION_POINT + LibAnalog.MIN_ACTUATION_DELTA);

    assertThat(keyBinding.isPressed()).isTrue();
  }
}
