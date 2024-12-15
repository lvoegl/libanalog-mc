package net.verotek.libanalog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.verotek.libanalog.api.AnalogEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LibAnalog implements ClientModInitializer {
  // TODO extract these to some setting
  public static final float ACTUATION_POINT = 0.5f;
  public static final float MIN_ACTUATION_DELTA = 0.1f;

  public static final String MOD_ID = "libanalog";

  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  private final AnalogEventHandler HANDLER = AnalogEventHandler.getInstance();

  @Override
  public void onInitializeClient() {
    LOGGER.info("Initializing analog keyboard handler");
    HANDLER.start();
    ClientLifecycleEvents.CLIENT_STOPPING.register(this::onShutdown);
  }

  private void onShutdown(MinecraftClient minecraft) {
    LOGGER.info("Shutting down analog keyboard handler");
    HANDLER.stop();
  }
}
