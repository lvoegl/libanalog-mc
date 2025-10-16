package net.verotek.libanalog.api;

import net.minecraft.client.util.InputUtil;
import net.verotek.libanalog.LibAnalog;

import java.util.concurrent.ConcurrentHashMap;

public final class AnalogKeyStates {

  private static final ConcurrentHashMap<InputUtil.Key, FloatHolder> AMOUNTS = new ConcurrentHashMap<>(64);

  private AnalogKeyStates() {}

  public static void set(InputUtil.Key key, float amount) {
    FloatHolder h = AMOUNTS.get(key);
    if (h == null) {
      h = AMOUNTS.computeIfAbsent(key, k -> new FloatHolder());
    }
    h.value = amount;
  }

  public static float get(InputUtil.Key key) {
    FloatHolder h = AMOUNTS.get(key);
    return h != null ? h.value : 0.0f;
  }

  public static boolean isPressed(InputUtil.Key key) {
    float amount = get(key);
    return amount >= LibAnalog.ACTUATION_POINT;
  }

  private static final class FloatHolder {
    volatile float value;
  }
}
