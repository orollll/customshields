package com.customshields.feature;

import com.customshields.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * Every client tick: if the crosshair is on a wind charge, attack at the configured CPS.
 * Nothing is queued, so attacking stops the moment the crosshair leaves the charge.
 */
public final class WindChargeAutoHit {
    private static double charge = 1.0;

    private WindChargeAutoHit() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(WindChargeAutoHit::onTick);
    }

    private static void onTick(MinecraftClient client) {
        ModConfig config = ModConfig.get();
        ClientPlayerEntity player = client.player;

        if (!config.autoHitEnabled || player == null || client.world == null
                || client.interactionManager == null || client.currentScreen != null) {
            charge = 1.0;
            return;
        }

        // Minecraft ticks 20 times per second, so CPS / 20 attacks are "earned" per tick.
        charge += config.cps / 20.0;

        Entity target = getWindChargeUnderCrosshair(client);
        if (target == null) {
            charge = Math.min(charge, 1.0); // ready for an instant first hit
            return;
        }

        if (charge >= 1.0) {
            charge -= 1.0;
            client.interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
        }
    }

    private static Entity getWindChargeUnderCrosshair(MinecraftClient client) {
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
            return null;
        }
        Entity entity = ((EntityHitResult) hit).getEntity();
        if (entity.isRemoved()) {
            return null;
        }
        EntityType<?> type = entity.getType();
        boolean isWindCharge = type == EntityType.WIND_CHARGE || type == EntityType.BREEZE_WIND_CHARGE;
        return isWindCharge ? entity : null;
    }
}
