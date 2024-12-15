package net.verotek.libanalog.interfaces.mixin;

public interface IAnalogKeybinding {

  float pressedAmount();

  void processAnalogEvent(int keyCode, float pressedAmount);

  void incrementTimesPressed();

  boolean analogActive();
}
