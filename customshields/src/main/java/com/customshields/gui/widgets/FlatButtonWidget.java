package com.customshields.gui.widgets;

import com.customshields.gui.UiDraw;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Dark button with a hover fade and an optional highlighted (accent border) state. */
public class FlatButtonWidget extends ButtonWidget implements Fadeable {
    private final Supplier<Text> label;
    private final BooleanSupplier highlighted;
    private float hover;
    private float fade = 1f;

    public FlatButtonWidget(int x, int y, int width, int height,
                            Supplier<Text> label, BooleanSupplier highlighted, Runnable action) {
        super(x, y, width, height, Text.empty(), button -> action.run(), DEFAULT_NARRATION_SUPPLIER);
        this.label = label;
        this.highlighted = highlighted;
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
        hover += ((isHovered() ? 1f : 0f) - hover) * 0.3f;

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        int border = highlighted.getAsBoolean() ? UiDraw.ACCENT
                : UiDraw.lerpRgb(UiDraw.BORDER, UiDraw.ACCENT, hover * 0.6f);
        int fill = UiDraw.lerpRgb(UiDraw.SURFACE, UiDraw.SURFACE_HOVER, hover);

        UiDraw.roundedRect(ctx, x, y, x + w, y + h, 4, UiDraw.argb(border, fade));
        UiDraw.roundedRect(ctx, x + 1, y + 1, x + w - 1, y + h - 1, 3, UiDraw.argb(fill, fade));

        ctx.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, label.get(),
                x + w / 2, y + (h - 8) / 2, UiDraw.argb(UiDraw.TEXT, fade));
    }
}
