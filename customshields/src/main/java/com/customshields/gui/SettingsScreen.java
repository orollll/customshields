package com.customshields.gui;

import com.customshields.config.ModConfig;
import com.customshields.gui.widgets.Fadeable;
import com.customshields.gui.widgets.FlatButtonWidget;
import com.customshields.gui.widgets.ModSliderWidget;
import com.customshields.gui.widgets.ToggleSwitchWidget;
import com.customshields.input.ModKeybinds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen extends Screen {
    private static final int PANEL_W = 320;
    private static final int PANEL_H = 290;

    private final Screen parent;
    private final List<Fadeable> fadeables = new ArrayList<>();
    private long openedAt;
    private int panelX;
    private int panelY;
    private KeyBinding listening; // the keybind waiting for a key press, or null

    public SettingsScreen(Screen parent) {
        super(Text.literal("Custom Shields"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        openedAt = Util.getMeasuringTimeMs();
        fadeables.clear();
        listening = null;

        panelX = (this.width - PANEL_W) / 2;
        panelY = (this.height - PANEL_H) / 2;
        int left = panelX + 20;
        int right = panelX + PANEL_W - 20;
        int inner = PANEL_W - 40;
        ModConfig config = ModConfig.get();

        track(new ToggleSwitchWidget(right - 44, panelY + 76, 44, 22,
                () -> config.autoHitEnabled,
                value -> {
                    config.autoHitEnabled = value;
                    ModConfig.save();
                }));

        track(new ModSliderWidget(left, panelY + 128, inner, 20));

        track(new FlatButtonWidget(right - 130, panelY + 178, 130, 20,
                () -> keyLabel(ModKeybinds.toggleKey),
                () -> listening == ModKeybinds.toggleKey,
                () -> listening = ModKeybinds.toggleKey));

        track(new FlatButtonWidget(right - 130, panelY + 204, 130, 20,
                () -> keyLabel(ModKeybinds.openMenuKey),
                () -> listening == ModKeybinds.openMenuKey,
                () -> listening = ModKeybinds.openMenuKey));

        track(new FlatButtonWidget(left, panelY + PANEL_H - 34, inner, 22,
                () -> Text.literal("Done"), () -> false, this::close));
    }

    private <T extends ClickableWidget & Fadeable> T track(T widget) {
        fadeables.add(widget);
        return addDrawableChild(widget);
    }

    private Text keyLabel(KeyBinding binding) {
        if (listening == binding) {
            return Text.literal("> Press a key <");
        }
        return binding.getBoundKeyLocalizedText();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (listening != null) {
            int code = input.key();
            InputUtil.Key newKey = null; // null = cancel
            if (code == GLFW.GLFW_KEY_DELETE || code == GLFW.GLFW_KEY_BACKSPACE) {
                newKey = InputUtil.UNKNOWN_KEY;
            } else if (code != GLFW.GLFW_KEY_ESCAPE) {
                newKey = InputUtil.Type.KEYSYM.createFromCode(code);
            }
            if (newKey != null) {
                listening.setBoundKey(newKey);
                KeyBinding.updateKeysByCode();
                if (this.client != null) {
                    this.client.options.write(); // keybinds are saved by Minecraft itself
                }
            }
            listening = null;
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        float t = MathHelper.clamp((Util.getMeasuringTimeMs() - openedAt) / 220f, 0f, 1f);
        float fade = 1f - (1f - t) * (1f - t); // ease-out
        for (Fadeable f : fadeables) {
            f.setFade(fade);
        }

        ModConfig config = ModConfig.get();
        int left = panelX + 20;
        int right = panelX + PANEL_W - 20;

        // Dim background, shadow, border, panel, accent strip
        ctx.fill(0, 0, this.width, this.height, UiDraw.argb(0x000000, 0.55f * fade));
        UiDraw.roundedRect(ctx, panelX - 2, panelY - 2, panelX + PANEL_W + 2, panelY + PANEL_H + 2, 8,
                UiDraw.argb(0x000000, 0.35f * fade));
        UiDraw.roundedRect(ctx, panelX, panelY, panelX + PANEL_W, panelY + PANEL_H, 6,
                UiDraw.argb(UiDraw.BORDER, fade));
        UiDraw.roundedRect(ctx, panelX + 1, panelY + 1, panelX + PANEL_W - 1, panelY + PANEL_H - 1, 5,
                UiDraw.argb(UiDraw.PANEL_BG, fade));
        ctx.fill(panelX + 8, panelY + 1, panelX + PANEL_W - 8, panelY + 3, UiDraw.argb(UiDraw.ACCENT, fade));

        if (fade > 0.1f) {
            // Title
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("CUSTOM SHIELDS").formatted(Formatting.BOLD),
                    this.width / 2, panelY + 12, UiDraw.argb(UiDraw.TEXT, fade));
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Wind Charge Auto-Hit"),
                    this.width / 2, panelY + 26, UiDraw.argb(UiDraw.MUTED, fade));

            // Status bar with pulsing dot
            boolean on = config.autoHitEnabled;
            int statusY = panelY + 46;
            int statusColor = on ? UiDraw.GREEN : UiDraw.RED;
            float pulse = on ? 0.65f + 0.35f * (float) Math.sin(Util.getMeasuringTimeMs() / 260.0) : 1f;
            UiDraw.roundedRect(ctx, left, statusY, right, statusY + 20, 4, UiDraw.argb(UiDraw.SURFACE, fade));
            UiDraw.roundedRect(ctx, left + 8, statusY + 6, left + 16, statusY + 14, 4,
                    UiDraw.argb(statusColor, fade * pulse));
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(on ? "ENABLED" : "DISABLED"),
                    left + 22, statusY + 6, UiDraw.argb(statusColor, fade));
            Text statusHint = Text.literal(on ? "Auto-hit is active" : "Auto-hit is off");
            ctx.drawTextWithShadow(this.textRenderer, statusHint,
                    right - 8 - this.textRenderer.getWidth(statusHint), statusY + 6,
                    UiDraw.argb(UiDraw.MUTED, fade));

            // Toggle row label
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("Auto-Hit"),
                    left, panelY + 83, UiDraw.argb(UiDraw.TEXT, fade));

            // Slider label + live value
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("Clicks per second"),
                    left, panelY + 112, UiDraw.argb(UiDraw.TEXT, fade));
            Text cpsText = Text.literal(config.cps + " CPS");
            ctx.drawTextWithShadow(this.textRenderer, cpsText,
                    right - this.textRenderer.getWidth(cpsText), panelY + 112,
                    UiDraw.argb(UiDraw.ACCENT, fade));

            // Keybinds section
            ctx.fill(left, panelY + 156, right, panelY + 157, UiDraw.argb(UiDraw.BORDER, fade));
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("KEYBINDS"),
                    left, panelY + 163, UiDraw.argb(UiDraw.MUTED, fade));
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("Toggle Auto-Hit"),
                    left, panelY + 184, UiDraw.argb(UiDraw.TEXT, fade));
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("Open this menu"),
                    left, panelY + 210, UiDraw.argb(UiDraw.TEXT, fade));
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Click a key box, then press a key. Esc cancels, Del unbinds."),
                    this.width / 2, panelY + 236, UiDraw.argb(UiDraw.MUTED, fade * 0.8f));
        }

        super.render(ctx, mouseX, mouseY, delta); // draws the widgets on top
    }

    /** Empty on purpose: we draw our own background and skip the vanilla blur. */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
        ModConfig.save();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
