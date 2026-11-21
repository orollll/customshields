package com.customshields;

import com.customshields.config.ModConfig;
import com.customshields.feature.WindChargeAutoHit;
import com.customshields.input.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomShieldsClient implements ClientModInitializer {
    public static final String MOD_ID = "customshields";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ModConfig.load();
        ModKeybinds.register();
        WindChargeAutoHit.register();
        LOGGER.info("Custom Shields loaded");
    }
}
