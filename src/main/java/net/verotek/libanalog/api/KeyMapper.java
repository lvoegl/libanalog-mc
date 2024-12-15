package net.verotek.libanalog.api;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.voegl.analogkey4j.key.HidKey;

public final class KeyMapper {

  private KeyMapper() {
    throw new UnsupportedOperationException();
  }

  private static <T, S> Map<T, S> reverseMap(Map<S, T> map) {
    HashMap<T, S> reversedHashMap = new HashMap<>();
    for (S key : map.keySet()) {
      reversedHashMap.put(map.get(key), key);
    }
    return reversedHashMap;
  }

  private static final Map<HidKey, Integer> HID_TO_GLFW =
      Collections.unmodifiableMap(
          new HashMap<>() {
            {
              put(HidKey.A, GLFW_KEY_A);
              put(HidKey.B, GLFW_KEY_B);
              put(HidKey.C, GLFW_KEY_C);
              put(HidKey.D, GLFW_KEY_D);
              put(HidKey.E, GLFW_KEY_E);
              put(HidKey.F, GLFW_KEY_F);
              put(HidKey.G, GLFW_KEY_G);
              put(HidKey.H, GLFW_KEY_H);
              put(HidKey.I, GLFW_KEY_I);
              put(HidKey.J, GLFW_KEY_J);
              put(HidKey.K, GLFW_KEY_K);
              put(HidKey.L, GLFW_KEY_L);
              put(HidKey.M, GLFW_KEY_M);
              put(HidKey.N, GLFW_KEY_N);
              put(HidKey.O, GLFW_KEY_O);
              put(HidKey.P, GLFW_KEY_P);
              put(HidKey.Q, GLFW_KEY_Q);
              put(HidKey.R, GLFW_KEY_R);
              put(HidKey.S, GLFW_KEY_S);
              put(HidKey.T, GLFW_KEY_T);
              put(HidKey.U, GLFW_KEY_U);
              put(HidKey.V, GLFW_KEY_V);
              put(HidKey.W, GLFW_KEY_W);
              put(HidKey.X, GLFW_KEY_X);
              put(HidKey.Y, GLFW_KEY_Y);
              put(HidKey.Z, GLFW_KEY_Z);
              put(HidKey.N0, GLFW_KEY_0);
              put(HidKey.N1, GLFW_KEY_1);
              put(HidKey.N2, GLFW_KEY_2);
              put(HidKey.N3, GLFW_KEY_3);
              put(HidKey.N4, GLFW_KEY_4);
              put(HidKey.N5, GLFW_KEY_5);
              put(HidKey.N6, GLFW_KEY_6);
              put(HidKey.N7, GLFW_KEY_7);
              put(HidKey.N8, GLFW_KEY_8);
              put(HidKey.N9, GLFW_KEY_9);
              put(HidKey.Space, GLFW_KEY_SPACE);
              put(HidKey.Comma, GLFW_KEY_COMMA);
              put(HidKey.Minus, GLFW_KEY_MINUS);
              put(HidKey.Period, GLFW_KEY_PERIOD);
              put(HidKey.Slash, GLFW_KEY_SLASH);
              put(HidKey.Semicolon, GLFW_KEY_SEMICOLON);
              put(HidKey.Equal, GLFW_KEY_EQUAL);
              put(HidKey.BracketLeft, GLFW_KEY_LEFT_BRACKET);
              put(HidKey.Backslash, GLFW_KEY_BACKSLASH);
              put(HidKey.BracketRight, GLFW_KEY_RIGHT_BRACKET);
              put(HidKey.Backquote, GLFW_KEY_GRAVE_ACCENT);
              put(HidKey.Escape, GLFW_KEY_ESCAPE);
              put(HidKey.Enter, GLFW_KEY_ENTER);
              put(HidKey.Tab, GLFW_KEY_TAB);
              put(HidKey.Backspace, GLFW_KEY_BACKSPACE);
              put(HidKey.Insert, GLFW_KEY_INSERT);
              put(HidKey.Delete, GLFW_KEY_DELETE);
              put(HidKey.ArrowRight, GLFW_KEY_RIGHT);
              put(HidKey.ArrowLeft, GLFW_KEY_LEFT);
              put(HidKey.ArrowDown, GLFW_KEY_DOWN);
              put(HidKey.ArrowUp, GLFW_KEY_UP);
              put(HidKey.PageUp, GLFW_KEY_PAGE_UP);
              put(HidKey.PageDown, GLFW_KEY_PAGE_DOWN);
              put(HidKey.Home, GLFW_KEY_HOME);
              put(HidKey.End, GLFW_KEY_END);
              put(HidKey.CapsLock, GLFW_KEY_CAPS_LOCK);
              put(HidKey.ScrollLock, GLFW_KEY_SCROLL_LOCK);
              put(HidKey.NumLock, GLFW_KEY_NUM_LOCK);
              put(HidKey.PrintScreen, GLFW_KEY_PRINT_SCREEN);
              put(HidKey.PauseBreak, GLFW_KEY_PAUSE);
              put(HidKey.F1, GLFW_KEY_F1);
              put(HidKey.F2, GLFW_KEY_F2);
              put(HidKey.F3, GLFW_KEY_F3);
              put(HidKey.F4, GLFW_KEY_F4);
              put(HidKey.F5, GLFW_KEY_F5);
              put(HidKey.F6, GLFW_KEY_F6);
              put(HidKey.F7, GLFW_KEY_F7);
              put(HidKey.F8, GLFW_KEY_F8);
              put(HidKey.F9, GLFW_KEY_F9);
              put(HidKey.F10, GLFW_KEY_F10);
              put(HidKey.F11, GLFW_KEY_F11);
              put(HidKey.F12, GLFW_KEY_F12);
              put(HidKey.F13, GLFW_KEY_F13);
              put(HidKey.F14, GLFW_KEY_F14);
              put(HidKey.F15, GLFW_KEY_F15);
              put(HidKey.F16, GLFW_KEY_F16);
              put(HidKey.F17, GLFW_KEY_F17);
              put(HidKey.F18, GLFW_KEY_F18);
              put(HidKey.F19, GLFW_KEY_F19);
              put(HidKey.F20, GLFW_KEY_F20);
              put(HidKey.F21, GLFW_KEY_F21);
              put(HidKey.F22, GLFW_KEY_F22);
              put(HidKey.F23, GLFW_KEY_F23);
              put(HidKey.F24, GLFW_KEY_F24);
              put(HidKey.Numpad1, GLFW_KEY_KP_1);
              put(HidKey.Numpad2, GLFW_KEY_KP_2);
              put(HidKey.Numpad3, GLFW_KEY_KP_3);
              put(HidKey.Numpad4, GLFW_KEY_KP_4);
              put(HidKey.Numpad5, GLFW_KEY_KP_5);
              put(HidKey.Numpad6, GLFW_KEY_KP_6);
              put(HidKey.Numpad7, GLFW_KEY_KP_7);
              put(HidKey.Numpad8, GLFW_KEY_KP_8);
              put(HidKey.Numpad9, GLFW_KEY_KP_9);
              put(HidKey.Numpad0, GLFW_KEY_KP_0);
              put(HidKey.NumpadDecimal, GLFW_KEY_KP_DECIMAL);
              put(HidKey.NumpadDivide, GLFW_KEY_KP_DIVIDE);
              put(HidKey.NumpadMultiply, GLFW_KEY_KP_MULTIPLY);
              put(HidKey.NumpadSubtract, GLFW_KEY_KP_SUBTRACT);
              put(HidKey.NumpadAdd, GLFW_KEY_KP_ADD);
              put(HidKey.NumpadEnter, GLFW_KEY_KP_ENTER);
              put(HidKey.NumpadEqual, GLFW_KEY_KP_EQUAL);
              put(HidKey.LeftCtrl, GLFW_KEY_LEFT_CONTROL);
              put(HidKey.LeftShift, GLFW_KEY_LEFT_SHIFT);
              put(HidKey.LeftAlt, GLFW_KEY_LEFT_ALT);
              put(HidKey.LeftMeta, GLFW_KEY_LEFT_SUPER);
              put(HidKey.RightCtrl, GLFW_KEY_RIGHT_CONTROL);
              put(HidKey.RightShift, GLFW_KEY_RIGHT_SHIFT);
              put(HidKey.RightAlt, GLFW_KEY_RIGHT_ALT);
              put(HidKey.RightMeta, GLFW_KEY_RIGHT_SUPER);
              put(HidKey.ContextMenu, GLFW_KEY_MENU);
            }
          });
  private static final Map<Integer, HidKey> GLFW_TO_HID =
      Collections.unmodifiableMap(reverseMap(HID_TO_GLFW));

  public static int hidToGlfw(HidKey key) {
    return HID_TO_GLFW.getOrDefault(key, -1);
  }

  public static HidKey glfwToHid(int code) {
    return GLFW_TO_HID.get(code);
  }
}
