package net.mystic.wallpapercraft.blocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InWorldHelper {

    @Nullable
    public static Block getIncrementedBlockColour(final IDecorativeBlock blockIn) {
        String ns = namespaceOf((Block) blockIn);
        if (ns == null) return null;

        Block b = getBlock(reg(ns, blockIn.getPattern(),
                ModBlocks.getNextColour(blockIn.getColour(), 1),
                blockIn.getSuffix(), blockIn));
        if (b == null) {
            b = getBlock(reg(ns, blockIn.getPattern(),
                    ModBlocks.getNextColour(blockIn.getColour(), 2),
                    blockIn.getSuffix(), blockIn));
        }
        return b;
    }

    @Nullable
    public static Block getBlockFromColourPress(final IDecorativeBlock blockIn, final PressColour pressColour) {
        String ns = namespaceOf((Block) blockIn);
        if (ns == null) return null;

        return getBlock(reg(ns, blockIn.getPattern(), pressColour.getColour(), blockIn.getSuffix(), blockIn));
    }

    @Nullable
    public static Block getBlockFromVariantPress(final IDecorativeBlock blockIn, final PressVariant pressVariant) {
        String ns = namespaceOf((Block) blockIn);
        if (ns == null) return null;

        return getBlock(reg(ns, blockIn.getPattern(), blockIn.getColour(), pressVariant.getVariant(), blockIn));
    }

    private static ResourceLocation reg(final String ns,
                                        final String pattern,
                                        final String colour,
                                        final String suffix,
                                        final IDecorativeBlock block) {
        return Wallpapercraft.getId(ns, pattern + colour + suffix + block.getPostfix());
    }

    private static @NotNull String namespaceOf(Block b) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(b);
        return key.getNamespace();
    }

    @Nullable
    private static Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
    }
}
