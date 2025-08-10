package net.mystic.wallpapercraft.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class WPTags extends BlockTagsProvider {

    public WPTags(PackOutput output,
                  CompletableFuture<HolderLookup.Provider> lookup,
                  @Nullable ExistingFileHelper efh) {
        super(output, lookup, Wallpapercraft.MODID, efh);
    }

    // WPTags.java
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        ModBlocks.AXE_MINABLE.forEach(supp -> tag(BlockTags.MINEABLE_WITH_AXE).add(supp.get()));
        ModBlocks.PICK_MINABLE.forEach(supp -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(supp.get()));
    }

    @Override
    public @NotNull String getName() {
        return "Wallpapercraft Block Tags";
    }
}
