package net.mystic.wallpapercraft.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.items.DecorativeItem;
import net.mystic.wallpapercraft.network.Network;
import net.mystic.wallpapercraft.network.VariantScrollRequest;
import net.mystic.wallpapercraft.util.MathUtil;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MouseScrollHandler {

    @SubscribeEvent
    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        final ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.isEmpty() || !(held.getItem() instanceof DecorativeItem) || !player.isCrouching()) return;
        var key = ForgeRegistries.ITEMS.getKey(held.getItem());
        if (key == null || !Wallpapercraft.MODID.equals(key.getNamespace())) return;

        final int delta = MathUtil.clamp((int) Math.round(event.getScrollDelta()), -1, 1);
        if (delta == 0) return;

        cycleVariant(held, delta);
        event.setCanceled(true);
    }

    private static void cycleVariant(ItemStack stack, int delta) {
        var itemKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemKey == null) return;

        Network.channel.sendToServer(new VariantScrollRequest(delta));
    }
}
