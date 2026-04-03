package net.verotek.libanalog;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LibAnalog implements ClientModInitializer {
  // TODO extract these to some setting
  public static final float ACTUATION_POINT = 0.5f;

  public static final String MOD_ID = "libanalog";

  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitializeClient() {}
}
