package net.verotek.libanalog.api;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.concurrent.ConcurrentHashMap;
import net.verotek.libanalog.LibAnalog;

public final class AnalogKeyStates {

  private static final ConcurrentHashMap<InputConstants.Key, FloatHolder> AMOUNTS =
      new ConcurrentHashMap<>(64);

  private AnalogKeyStates() {}

  public static void set(InputConstants.Key key, float amount) {
    FloatHolder h = AMOUNTS.get(key);
    if (h == null) {
      h = AMOUNTS.computeIfAbsent(key, k -> new FloatHolder());
    }
    h.value = amount;
  }

  public static float get(InputConstants.Key key) {
    FloatHolder h = AMOUNTS.get(key);
    return h != null ? h.value : 0.0f;
  }

  public static boolean isPressed(InputConstants.Key key) {
    float amount = get(key);
    return amount >= LibAnalog.ACTUATION_POINT;
  }

  private static final class FloatHolder {
    volatile float value;
  }
}
