package net.mystic.wallpapercraft.blocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mystic.wallpapercraft.Wallpapercraft;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {

    public static final String[] COLOURS = {"blue", "brown", "cyan", "gray", "green", "purple", "red", "yellow"};
    public static final String[] PATTERNS = {
            "auralamp", "solid", "brick", "checkeredwool", "clay", "colouredbrick", "damask", "diagonallydotted", "dotted",
            "fancytiles", "floral", "frostedglass", "jewel", "rippled", "stamp", "stonebrick", "stonelamp", "striped",
            "texturedglass", "tintedglass", "woodplank", "wool"
    };

    public static final DeferredRegister<Block> BLOCKS_DR =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Wallpapercraft.MODID);

    public static final Map<String, RegistryObject<Block>> BLOCKS = new HashMap<>();

    public static void register(IEventBus modBus) {
        BLOCKS_DR.register(modBus);
    }

    public static void bootstrap() {
        registerColouredBlocks("auralamp", stoneProps(1.5f, SoundType.LANTERN), true);
        registerColouredBlocks("stonelamp", stoneProps(1.5f, SoundType.LANTERN), true);
        registerColouredBlocks("frostedglass", glassProps(), false);
        registerColouredBlocks("texturedglass", glassProps(), false);
        registerColouredBlocks("tintedglass", glassProps(), false);
        registerColouredBlocks("checkeredwool", woolProps(0.8f), false);
        registerColouredBlocks("wool", woolProps(0.8f), false);
        registerColouredBlocks("clay", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("woodplank", woodProps(), false);
        registerColouredBlocks("brick", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("colouredbrick", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("damask", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("diagonallydotted", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("dotted", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("fancytiles", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("floral", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("rippled", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("solid", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("stonebrick", stoneProps(1.5f, SoundType.STONE), false);
        registerColouredBlocks("striped", stoneProps(1.5f, SoundType.STONE), false);
        registerCarpets(woolProps(0.1f));
        registerSimple("compressed", stoneProps(2.0f, SoundType.STONE));
        registerSimple("hardened", stoneProps(2.0f, SoundType.STONE));
    }

    private static void registerSimple(String name, BlockBehaviour.Properties props) {
        RegistryObject<Block> ro = BLOCKS_DR.register(name,
                () -> new DecorativeBlockPatterned(name, "none", 0, props, 0));
        BLOCKS.put(name, ro);
    }

    private static void registerColouredBlocks(String pattern, BlockBehaviour.Properties baseProps, boolean isLight) {
        for (final String colour : COLOURS) {
            int suffixCount = colour.equals("cyan") ? 9 : 14;
            for (int suffix = 0; suffix <= suffixCount; suffix++) {
                int light = isLight ? 15 : 0;
                boolean isGlass = pattern.contains("glass");

                String regName = pattern + colour + "-" + suffix;

                int finalSuffix = suffix;
                RegistryObject<Block> ro = BLOCKS_DR.register(regName, () ->
                        isGlass
                                ? new DecorativeBlockGlass(pattern, colour, finalSuffix, baseProps, light)
                                : new DecorativeBlockPatterned(pattern, colour, finalSuffix, baseProps, light)
                );
                BLOCKS.put(regName, ro);
            }
        }
    }

    private static void registerCarpets(BlockBehaviour.Properties baseProps) {
        for (final String colour : COLOURS) {
            int suffixCount = colour.equals("cyan") ? 9 : 14;
            for (int suffix = 0; suffix <= suffixCount; suffix++) {
                String regName = "wool" + colour + "-" + suffix + "_carpet";
                int finalSuffix = suffix;
                RegistryObject<Block> ro = BLOCKS_DR.register(regName,
                        () -> new DecorativeCarpet("wool", colour, finalSuffix, baseProps));
                BLOCKS.put(regName, ro);
            }
        }
    }

    private static BlockBehaviour.Properties stoneProps(float hardness, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .strength(hardness)
                .sound(sound);
    }

    private static BlockBehaviour.Properties woodProps() {
        return BlockBehaviour.Properties.of()
                .strength((float) 1.5)
                .sound(SoundType.WOOD);
    }

    private static BlockBehaviour.Properties woolProps(float hardness) {
        return BlockBehaviour.Properties.of()
                .strength(hardness)
                .sound(SoundType.WOOL);
    }

    private static BlockBehaviour.Properties glassProps() {
        return BlockBehaviour.Properties.of()
                .strength((float) 0.3)
                .noOcclusion()
                .sound(SoundType.GLASS);
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

    @Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ClientSetup {
        @SubscribeEvent
        public static void onClient(FMLClientSetupEvent e) {
            e.enqueueWork(() -> {
                var translucent = RenderType.translucent();
                BLOCKS.forEach((name, ro) -> {
                    Block b = ro.get();
                    if (b instanceof DecorativeBlockGlass) {
                        ItemBlockRenderTypes.setRenderLayer(b, translucent);
                    }
                });
            });
        }
    }
}
