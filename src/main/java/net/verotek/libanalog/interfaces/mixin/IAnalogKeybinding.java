package net.verotek.libanalog.interfaces.mixin;

public interface IAnalogKeybinding {

  float pressedAmount();

  void processAnalogEvent(int keyCode, float pressedAmount, boolean isInMenu);

  void incrementTimesPressed();

  boolean analogActive();
}
