package net.mystic.wallpapercraft.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.DecorativeItem;
import net.mystic.wallpapercraft.network.VariantScrollPayload;
import net.mystic.wallpapercraft.util.MathUtil;

@EventBusSubscriber(modid = Wallpapercraft.MODID, value = Dist.CLIENT)
public class MouseScrollHandler {

    @SubscribeEvent
    public static void onScroll(net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.isEmpty() || !(held.getItem() instanceof DecorativeItem) || !player.isCrouching()) return;

        var key = BuiltInRegistries.ITEM.getKey(held.getItem());
        if (!Wallpapercraft.MODID.equals(key.getNamespace())) return;

        int delta = MathUtil.clamp((int)Math.round(event.getScrollDeltaY()), -1, 1);
        if (delta == 0) return;
        var conn = mc.getConnection();
        if (conn != null) {
            conn.send(new VariantScrollPayload(delta));
            event.setCanceled(true);
        }
    }
}

