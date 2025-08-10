package net.mystic.wallpapercraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class Tags extends BlockTagsProvider {
    public Tags(PackOutput output,
                CompletableFuture<HolderLookup.Provider> lookup,
                @Nullable ExistingFileHelper efh) {
        super(output, lookup, Wallpapercraft.MODID, efh);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (RegistryObject<Block> def : ModBlocks.BLOCKS.values()) {
            Block block = def.get();
            SoundType s = block.defaultBlockState().getSoundType();

            if (s == SoundType.WOOD || s == SoundType.WOOL || s == SoundType.BAMBOO || s == SoundType.BAMBOO_WOOD) {
                tag(BlockTags.MINEABLE_WITH_AXE).add(block);
            } else {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            }
        }
    }

    @Override public @NotNull String getName() { return "Wallpapercraft Block Tags"; }
}
