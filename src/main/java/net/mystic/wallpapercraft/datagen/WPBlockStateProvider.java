package net.mystic.wallpapercraft.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.DecorativeBlockGlass;
import net.mystic.wallpapercraft.blocks.DecorativeCarpet;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public final class WPBlockStateProvider extends BlockStateProvider {

    public WPBlockStateProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, Wallpapercraft.MODID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        ModBlocks.BLOCKS.values().forEach(holder -> {
            Block b = holder.get();
            String name = BuiltInRegistries.BLOCK.getKey(b).getPath();

            if (b instanceof DecorativeCarpet decoCarpet) {
                String woolTex = "block/wool/" + "wool" + decoCarpet.getColour() + decoCarpet.getSuffix();
                ModelFile model = models()
                        .withExistingParent(name, mcLoc("block/carpet"))
                        .texture("wool", modLoc(woolTex));
                simpleBlock(b, model);
                return;
            }

            if (b instanceof IDecorativeBlock deco) {
                final String texPath = deco.getColour().equals("none")
                        ? "block/" + deco.getPattern()
                        : "block/" + deco.getPattern() + "/" + deco.getPattern() + deco.getColour() + deco.getSuffix();

                BlockModelBuilder model = models().cubeAll(name, modLoc(texPath));
                if (b instanceof DecorativeBlockGlass) {
                    model.renderType("translucent");
                }
                simpleBlock(b, model);
                return;
            }

            simpleBlock(b, models().cubeAll(name, modLoc("block/" + name)));
        });
    }

    public @NotNull ResourceLocation blockTexture(@NotNull Block b) {
        return modLoc("block/" + BuiltInRegistries.BLOCK.getKey(b).getPath());
    }
}
