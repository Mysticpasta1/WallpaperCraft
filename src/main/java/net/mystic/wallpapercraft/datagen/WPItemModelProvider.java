package net.mystic.wallpapercraft.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.DecorativeCarpet;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class WPItemModelProvider extends ItemModelProvider {
    public WPItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, Wallpapercraft.MODID, efh);
    }

    @Override
    protected void registerModels() {
        ModBlocks.BLOCKS.values().forEach(holder -> {
            Block b = holder.get();
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            String name = id.getPath();

            if (b instanceof DecorativeCarpet deco) {
                getBuilder(name)
                        .parent(getExistingFile(mcLoc("block/carpet")))
                        .texture("wool", modLoc("block/wool/wool" + deco.getColour() + deco.getSuffix()));
            } else {
                withExistingParent(name, modLoc("block/" + name));
            }
        });

        handheldItem();
        simpleItem("pressblank");
        for (String p : ModBlocks.PATTERNS) {
            simpleItem("press" + p.toLowerCase());
        }

        for (String c : ModBlocks.COLOURS) {
            itemWithTexture("press" + c.toLowerCase(), "presscolour" + c.toLowerCase());
        }

        for (int i = 0; i <= 14; i++) {
            itemWithTexture("pressvariant" + i, "pressvariant-" + (i + 1));
        }
    }

    private void simpleItem(String modelName) {
        getBuilder(modelName)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/" + modelName));
    }

    private void itemWithTexture(String modelName, String textureName) {
        getBuilder(modelName)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/" + textureName));
    }

    private void handheldItem() {
        getBuilder("paintbrush")
                .parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", modLoc("item/" + "paintbrush"));
    }
}
