package com.customshields.input;

import com.customshields.config.ModConfig;
import com.customshields.gui.SettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ModKeybinds {
    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("customshields", "main"));

    public static KeyBinding toggleKey;
    public static KeyBinding openMenuKey;

    private ModKeybinds() {}

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.customshields.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.customshields.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(ModKeybinds::onTick);
    }

    private static void onTick(MinecraftClient client) {
        ModConfig config = ModConfig.get();

        while (toggleKey.wasPressed()) {
            config.autoHitEnabled = !config.autoHitEnabled;
            ModConfig.save();
            if (client.player != null) {
                Text state = config.autoHitEnabled
                        ? Text.literal("ON").formatted(Formatting.GREEN)
                        : Text.literal("OFF").formatted(Formatting.RED);
                client.player.sendMessage(Text.literal("Wind Charge Auto-Hit: ").append(state), true);
            }
        }

        while (openMenuKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new SettingsScreen(null));
            }
        }
    }
}
