package net.mystic.wallpapercraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.NetworkEvent;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.items.DecorativeItem;
import net.mystic.wallpapercraft.items.ModItems;
import net.mystic.wallpapercraft.util.MathUtil;

import java.util.function.Supplier;

public class VariantScrollRequest {

    private int delta;

    public VariantScrollRequest() {
    }

    public VariantScrollRequest(final int delta) {
        this.delta = delta;
    }

    public static VariantScrollRequest fromBytes(FriendlyByteBuf buf) {
        VariantScrollRequest pkt = new VariantScrollRequest();
        pkt.delta = MathUtil.clamp(buf.readInt(), -1, 1);
        return pkt;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.delta);
    }

    public static void handle(VariantScrollRequest packet, Supplier<NetworkEvent.Context> ctx) {
        final NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player == null) return;

            final ItemStack stack = player.getMainHandItem();
            if (!player.isCrouching() || stack.isEmpty() || !(stack.getItem() instanceof DecorativeItem)) {
                return;
            }

            final var itemKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (itemKey == null) return;

            final String path = itemKey.getPath();
            final IDecorativeBlock block = (IDecorativeBlock) ModBlocks.BLOCKS.get(path).get();

            final int limit = block.getColour().contains("cyan") ? 9 : 14;
            final int current = Math.abs(Integer.parseInt(block.getSuffix()));
            final int next = MathUtil.rollOver(current + packet.delta, 0, limit);

            final DecorativeItem nextItem =
                    ModItems.get(block.getPattern(), block.getColour(), next, block.getPostfix());

            if (nextItem != null) {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(nextItem, stack.getCount()));
            }
        });
        context.setPacketHandled(true);
    }
}
