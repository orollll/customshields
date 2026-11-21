package com.customshields.config;

import com.customshields.CustomShieldsClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Settings saved to .minecraft/config/customshields.json */
public final class ModConfig {
    public static final int MIN_CPS = 1;
    public static final int MAX_CPS = 20;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
            FabricLoader.getInstance().getConfigDir().resolve("customshields.json");
    private static ModConfig instance = new ModConfig();

    public boolean autoHitEnabled = false;
    public int cps = 10;

    public static ModConfig get() {
        return instance;
    }

    public static void load() {
        if (Files.exists(PATH)) {
            try {
                ModConfig loaded = GSON.fromJson(Files.readString(PATH), ModConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } catch (IOException | RuntimeException e) {
                CustomShieldsClient.LOGGER.error("Could not read config, using defaults", e);
            }
        }
        instance.cps = Math.max(MIN_CPS, Math.min(MAX_CPS, instance.cps));
        save();
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(instance));
        } catch (IOException e) {
            CustomShieldsClient.LOGGER.error("Could not save config", e);
        }
    }
}
