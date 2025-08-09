package net.mystic.wallpapercraft.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressVariant;

public interface IDecorativeBlock {

    String getPostfix();
    String getNameForRegistry();
    String getPattern();
    String getColour();
    String getSuffix();

    default void onBlockClicked(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        if (level.isClientSide) return;

        var held = player.getMainHandItem();
        if (held.isEmpty()) return;

        var heldItem = held.getItem();
        Block target = null;

        // paintbrush check via registry key (no ModItems field)
        var paintbrush = ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("paintbrush"));
        if (heldItem == paintbrush) {
            target = InWorldHelper.getIncrementedBlockColour(this);
        } else if (heldItem instanceof PressColour colour) {
            target = InWorldHelper.getBlockFromColourPress(this, colour);
        } else if (heldItem instanceof PressVariant variant) {
            target = InWorldHelper.getBlockFromVariantPress(this, variant);
        }

        if (target == null) return;

        level.setBlock(pos, target.defaultBlockState(), 3);
    }
}
