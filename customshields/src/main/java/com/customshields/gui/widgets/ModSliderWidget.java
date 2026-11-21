package com.customshields.gui.widgets;

import com.customshields.config.ModConfig;
import com.customshields.gui.UiDraw;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/** Slider that edits ModConfig.cps (whole numbers, 1..20). */
public class ModSliderWidget extends SliderWidget implements Fadeable {
    private float fade = 1f;

    public ModSliderWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty(), toSlider(ModConfig.get().cps));
        updateMessage();
    }

    private static double toSlider(int cps) {
        return (cps - ModConfig.MIN_CPS) / (double) (ModConfig.MAX_CPS - ModConfig.MIN_CPS);
    }

    @Override
    public void setFade(float fade) {
        this.fade = fade;
    }

    @Override
    protected void updateMessage() {
        setMessage(Text.literal(ModConfig.get().cps + " CPS"));
    }

    @Override
    protected void applyValue() {
        int cps = ModConfig.MIN_CPS
                + (int) Math.round(this.value * (ModConfig.MAX_CPS - ModConfig.MIN_CPS));
        ModConfig.get().cps = cps;
        this.value = toSlider(cps); // snap to whole numbers
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (fade < 0.05f) {
            return;
        }
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        int trackY = y + h / 2 - 2;
        int knobW = 10;
        int knobX = x + (int) Math.round(this.value * (w - knobW));

        UiDraw.roundedRect(ctx, x, trackY, x + w, trackY + 4, 2, UiDraw.argb(UiDraw.TRACK_OFF, fade));
        UiDraw.roundedRect(ctx, x, trackY, knobX + knobW / 2, trackY + 4, 2,
                UiDraw.argb(UiDraw.ACCENT, fade));

        int knobColor = isHovered() ? 0xFFFFFF : 0xD5DEFF;
        UiDraw.roundedRect(ctx, knobX, y + 2, knobX + knobW, y + h - 2, 3, UiDraw.argb(knobColor, fade));
    }
}
