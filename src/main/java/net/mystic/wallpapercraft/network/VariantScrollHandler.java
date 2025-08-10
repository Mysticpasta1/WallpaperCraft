package net.mystic.wallpapercraft.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.items.DecorativeItem;
import net.mystic.wallpapercraft.items.ModItems;
import net.mystic.wallpapercraft.util.MathUtil;

public final class VariantScrollHandler {
    private VariantScrollHandler() {}

    public static void handle(final VariantScrollPayload pkt, final IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer player)) return;

        ItemStack stack = player.getMainHandItem();
        if (!player.isCrouching() || stack.isEmpty() || !(stack.getItem() instanceof DecorativeItem)) return;
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = itemKey.getPath();
        IDecorativeBlock deco = null;
        Object entry = ModBlocks.BLOCKS.get(path);
        if (entry instanceof IDecorativeBlock d) {
            deco = d;
        }
        if (deco == null) return;

        int limit   = deco.getColour().contains("cyan") ? 9 : 14;
        int current = Math.abs(Integer.parseInt(deco.getSuffix().replaceFirst("^-","")));
        int delta   = MathUtil.clamp(pkt.delta(), -1, 1);
        int next    = MathUtil.rollOver(current + delta, 0, limit);

        DecorativeItem nextItem = ModItems.get(deco.getPattern(), deco.getColour(), next, deco.getPostfix());
        if (nextItem != null) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(nextItem, stack.getCount()));
        }
    }
}
