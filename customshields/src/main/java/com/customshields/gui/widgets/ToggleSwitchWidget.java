package com.customshields.gui.widgets;

import com.customshields.gui.UiDraw;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Animated ON/OFF pill switch. */
public class ToggleSwitchWidget extends ButtonWidget implements Fadeable {
    private final BooleanSupplier state;
    private float progress;
    private float fade = 1f;

    public ToggleSwitchWidget(int x, int y, int width, int height,
                              BooleanSupplier state, Consumer<Boolean> onChange) {
        super(x, y, width, height, Text.empty(),
                button -> onChange.accept(!state.getAsBoolean()), DEFAULT_NARRATION_SUPPLIER);
        this.state = state;
        this.progress = state.getAsBoolean() ? 1f : 0f;
    }

    @Override
    public void setFade(float fade) {
        this.fade = fade;
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (fade < 0.05f) {
            return;
        }
        progress += ((state.getAsBoolean() ? 1f : 0f) - progress) * 0.25f;

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        if (isHovered()) {
            UiDraw.roundedRect(ctx, x - 1, y - 1, x + w + 1, y + h + 1, h / 2 + 1,
                    UiDraw.argb(UiDraw.ACCENT, fade * 0.8f));
        }
        int track = UiDraw.lerpRgb(UiDraw.TRACK_OFF, UiDraw.GREEN, progress);
        UiDraw.roundedRect(ctx, x, y, x + w, y + h, h / 2, UiDraw.argb(track, fade));

        int knob = h - 6;
        int knobX = x + 3 + Math.round(progress * (w - knob - 6));
        UiDraw.roundedRect(ctx, knobX, y + 3, knobX + knob, y + 3 + knob, knob / 2,
                UiDraw.argb(0xFFFFFF, fade));
    }
}
