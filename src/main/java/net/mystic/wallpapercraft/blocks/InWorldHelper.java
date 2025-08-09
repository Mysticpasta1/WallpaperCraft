package net.mystic.wallpapercraft.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressVariant;

import javax.annotation.Nullable;

public class InWorldHelper {

    @Nullable
    public static Block getIncrementedBlockColour(final IDecorativeBlock blockIn) {
        String ns = namespaceOf((Block) blockIn);
        if (ns == null) return null;

        // try +1, then +2
        Block b = ForgeRegistries.BLOCKS.getValue(
                reg(ns, blockIn.getPattern(), ModBlocks.getNextColour(blockIn.getColour(), 1), blockIn.getSuffix(), blockIn)
        );
        if (b == null) {
            b = ForgeRegistries.BLOCKS.getValue(
                    reg(ns, blockIn.getPattern(), ModBlocks.getNextColour(blockIn.getColour(), 2), blockIn.getSuffix(), blockIn)
            );
        }
        return b; // may be null if not found
    }

    @Nullable
    public static Block getBlockFromColourPress(final IDecorativeBlock blockIn, final PressColour pressColour) {
        String ns = namespaceOf((Block) blockIn);
        if (ns == null) return null;

        return ForgeRegistries.BLOCKS.getValue(
                reg(ns, blockIn.getPattern(), pressColour.getColour(), blockIn.getSuffix(), blockIn)
        );
    }

    @Nullable
    public static Block getBlockFromVariantPress(final IDecorativeBlock blockIn, final PressVariant pressVariant) {
        String ns = namespaceOf((Block) blockIn);
        if (ns == null) return null;

        return ForgeRegistries.BLOCKS.getValue(
                reg(ns, blockIn.getPattern(), blockIn.getColour(), pressVariant.getVariant(), blockIn)
        );
    }

    private static ResourceLocation reg(final String ns, final String pattern, final String colour, final String suffix, final IDecorativeBlock block) {
        return Wallpapercraft.getId(ns, pattern + colour + suffix + block.getPostfix());
    }

    @Nullable
    private static String namespaceOf(Block b) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(b);
        return key == null ? null : key.getNamespace();
    }
}
