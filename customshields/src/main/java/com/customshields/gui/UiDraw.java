package com.customshields.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

/** Colours (RGB) and small drawing helpers for the dark theme. */
public final class UiDraw {
    public static final int PANEL_BG = 0x14161C;
    public static final int BORDER = 0x2B3040;
    public static final int SURFACE = 0x1C1F29;
    public static final int SURFACE_HOVER = 0x2A2F3E;
    public static final int ACCENT = 0x5B8CFF;
    public static final int TEXT = 0xE8EAF0;
    public static final int MUTED = 0x8A90A2;
    public static final int GREEN = 0x4ADE80;
    public static final int RED = 0xF87171;
    public static final int TRACK_OFF = 0x3A3F50;

    private UiDraw() {}

    /** Combines an RGB colour with an alpha (0..1) into the ARGB int Minecraft expects. */
    public static int argb(int rgb, float alpha) {
        int a = Math.round(MathHelper.clamp(alpha, 0f, 1f) * 255f);
        return (a << 24) | (rgb & 0xFFFFFF);
    }

    public static int lerpRgb(int from, int to, float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        int r = Math.round(((from >> 16) & 255) + (((to >> 16) & 255) - ((from >> 16) & 255)) * t);
        int g = Math.round(((from >> 8) & 255) + (((to >> 8) & 255) - ((from >> 8) & 255)) * t);
        int b = Math.round((from & 255) + ((to & 255) - (from & 255)) * t);
        return (r << 16) | (g << 8) | b;
    }

    /** Filled rectangle with rounded corners. */
    public static void roundedRect(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int argb) {
        int r = Math.min(radius, Math.min((x2 - x1) / 2, (y2 - y1) / 2));
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            ctx.fill(x1 + inset, y1 + i, x2 - inset, y1 + i + 1, argb);
            ctx.fill(x1 + inset, y2 - i - 1, x2 - inset, y2 - i, argb);
        }
        if (y2 - y1 - 2 * r > 0) {
            ctx.fill(x1, y1 + r, x2, y2 - r, argb);
        }
    }
}
