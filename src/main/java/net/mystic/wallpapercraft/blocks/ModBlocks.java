package net.mystic.wallpapercraft.blocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import net.mystic.wallpapercraft.Wallpapercraft;

@Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {

    public static final String[] COLOURS = { "blue", "brown", "cyan", "gray", "green", "purple", "red", "yellow" };
    public static final String[] PATTERNS = { "auralamp", "solid", "brick", "checkeredwool", "clay", "colouredbrick",
            "damask", "diagonallydotted", "dotted", "fancytiles", "floral", "frostedglass",
            "jewel", "rippled", "stamp", "stonebrick", "stonelamp", "striped",
            "texturedglass", "tintedglass", "woodplank", "wool" };

    /** name -> block instance */
    public static final Map<String, IDecorativeBlock> BLOCKS = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterBlocks(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) return;

        event.register(ForgeRegistries.Keys.BLOCKS, helper -> {
            // Light-emitting
            registerColouredBlocks(helper, "auralamp",   Material.STONE, SoundType.LANTERN, true);
            registerColouredBlocks(helper, "stonelamp",  Material.STONE, SoundType.LANTERN, true);

            // Glass family
            registerColouredBlocks(helper, "frostedglass",  Material.GLASS, SoundType.GLASS, false);
            registerColouredBlocks(helper, "texturedglass", Material.GLASS, SoundType.GLASS, false);
            registerColouredBlocks(helper, "tintedglass",   Material.GLASS, SoundType.GLASS, false);

            // Wool family
            registerColouredBlocks(helper, "checkeredwool", Material.WOOL, SoundType.WOOL, false);
            registerColouredBlocks(helper, "wool",          Material.WOOL, SoundType.WOOL, false);

            // Clay
            registerColouredBlocks(helper, "clay", Material.CLAY, SoundType.STONE, false);

            // Wood
            registerColouredBlocks(helper, "woodplank", Material.WOOD, SoundType.WOOD, false);

            // The rest
            registerColouredBlocks(helper, "brick",            Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "colouredbrick",    Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "damask",           Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "diagonallydotted", Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "dotted",           Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "fancytiles",       Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "floral",           Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "rippled",          Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "solid",            Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "stonebrick",       Material.STONE, SoundType.STONE, false);
            registerColouredBlocks(helper, "striped",          Material.STONE, SoundType.STONE, false);

            // Carpets (wool textures only)
            registerCarpets(helper, "wool", Material.WOOL, SoundType.WOOL, false);

            // Simple blocks
            helper.register(Wallpapercraft.getId("compressed"),
                    new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f)));
            helper.register(Wallpapercraft.getId("hardened"),
                    new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f)));
        });
    }

    public static String getNextColour(final String colour, final int increment) {
        int index = IntStream.range(0, COLOURS.length)
                .filter(i -> colour.equals(COLOURS[i]))
                .findFirst().orElse(-1);

        index += increment;
        if (increment > 0 && index >= COLOURS.length) index = 0;
        else if (increment < 0 && index < 0) index = COLOURS.length - 1;
        return COLOURS[index];
    }

    private static void registerColouredBlocks(
            RegisterEvent.RegisterHelper<Block> helper,
            final String pattern, final Material material,
            SoundType soundType, final boolean isLight
    ) {
        for (final String s : COLOURS) {
            final int suffixCount = s.equals("cyan") ? 9 : 14;

            for (int suffix = 0; suffix <= suffixCount; suffix++) {
                final IDecorativeBlock deco;
                final int light = isLight ? 15 : 0;

                if (material == Material.GLASS) {
                    deco = new DecorativeBlockGlass(pattern, s, suffix, material, soundType, 0.3f, light);
                } else {
                    deco = new DecorativeBlockPatterned(pattern, s, suffix, material, soundType, 1.5f, light);
                }

                final String regName = deco.getNameForRegistry();
                helper.register(Wallpapercraft.getId(regName), (Block) deco);
                BLOCKS.put(regName, deco);
            }
        }
    }

    private static void registerCarpets(
            RegisterEvent.RegisterHelper<Block> helper,
            final String pattern, final Material material,
            SoundType soundType, final boolean isLight
    ) {
        for (final String s : COLOURS) {
            final int suffixCount = s.equals("cyan") ? 9 : 14;

            for (int suffix = 0; suffix <= suffixCount; suffix++) {
                final DecorativeCarpet carpet =
                        new DecorativeCarpet(pattern, s, suffix, DyeColor.WHITE, material, soundType, 0.8f, 0);

                final String regName = carpet.getNameForRegistry();
                helper.register(Wallpapercraft.getId(regName), carpet);
                BLOCKS.put(regName, carpet);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ClientSetup {
        @SubscribeEvent
        public static void onClient(FMLClientSetupEvent e) {
            e.enqueueWork(() -> {
                var translucent = RenderType.translucent();
                ModBlocks.BLOCKS.values().stream()
                        .filter(b -> b instanceof DecorativeBlockGlass)
                        .map(b -> (Block) b)
                        .forEach(b -> ItemBlockRenderTypes.setRenderLayer(b, translucent));
            });
        }
    }
}
