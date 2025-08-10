package net.mystic.wallpapercraft.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.mystic.wallpapercraft.Wallpapercraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ModBlocks {

    public static final String[] COLOURS = {"blue", "brown", "cyan", "gray", "green", "purple", "red", "yellow"};
    public static final String[] PATTERNS = {
            "auralamp", "solid", "brick", "checkeredwool", "clay", "colouredbrick", "damask", "diagonallydotted", "dotted",
            "fancytiles", "floral", "frostedglass", "jewel", "rippled", "stamp", "stonebrick", "stonelamp", "striped",
            "texturedglass", "tintedglass", "woodplank", "wool"
    };

    public static final DeferredRegister<Block> BLOCKS_DR =
            DeferredRegister.create(Registries.BLOCK, Wallpapercraft.MODID);

    public static final Map<String, DeferredHolder<Block, ? extends Block>> BLOCKS = new HashMap<>();

    public enum Tool {AXE, PICK}

    public static final Set<Supplier<? extends Block>> AXE_MINABLE = new HashSet<>();
    public static final Set<Supplier<? extends Block>> PICK_MINABLE = new HashSet<>();

    public record BlockProps(BlockBehaviour.Properties props, Tool tool) {
    }

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

    private static void registerSimple(String name, BlockProps bp) {
        DeferredHolder<Block, DecorativeBlockPatterned> ro = BLOCKS_DR.register(name,
                () -> new DecorativeBlockPatterned(name, "none", 0, bp.props(), 0));
        BLOCKS.put(name, ro);
        markTool(ro, bp.tool());
    }

    private static void registerColouredBlocks(String pattern, BlockProps bp, boolean isLight) {
        for (final String colour : COLOURS) {
            int suffixCount = colour.equals("cyan") ? 9 : 14;
            for (int suffix = 0; suffix <= suffixCount; suffix++) {
                int light = isLight ? 15 : 0;
                boolean isGlass = pattern.contains("glass");

                String regName = pattern + colour + "-" + suffix;
                int finalSuffix = suffix;

                DeferredHolder<Block, ? extends HalfTransparentBlock> ro = BLOCKS_DR.register(regName, () ->
                        isGlass
                                ? new DecorativeBlockGlass(pattern, colour, finalSuffix, bp.props(), light)
                                : new DecorativeBlockPatterned(pattern, colour, finalSuffix, bp.props(), light)
                );
                BLOCKS.put(regName, ro);
                markTool(ro, bp.tool());
            }
        }
    }

    private static void registerCarpets(BlockProps bp) {
        for (final String colour : COLOURS) {
            int suffixCount = colour.equals("cyan") ? 9 : 14;
            for (int suffix = 0; suffix <= suffixCount; suffix++) {
                String regName = "wool" + colour + "-" + suffix + "_carpet";
                int finalSuffix = suffix;
                DeferredHolder<Block, DecorativeCarpet> ro = BLOCKS_DR.register(regName,
                        () -> new DecorativeCarpet("wool", colour, finalSuffix, bp.props()));
                BLOCKS.put(regName, ro);
                markTool(ro, bp.tool());
            }
        }
    }

    private static void markTool(Supplier<? extends Block> sup, Tool t) {
        if (t == Tool.AXE) AXE_MINABLE.add(sup);
        else PICK_MINABLE.add(sup);
    }

    private static BlockProps stoneProps(float hardness, SoundType sound) {
        return new BlockProps(BlockBehaviour.Properties.of().strength(hardness).sound(sound), Tool.PICK);
    }

    private static BlockProps woodProps() {
        return new BlockProps(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.WOOD), Tool.AXE);
    }

    private static BlockProps woolProps(float hardness) {
        return new BlockProps(BlockBehaviour.Properties.of().strength(hardness).sound(SoundType.WOOL), Tool.AXE);
    }

    private static BlockProps glassProps() {
        return new BlockProps(BlockBehaviour.Properties.of().strength(0.3f).noOcclusion().sound(SoundType.GLASS), Tool.PICK);
    }

    public static String getNextColour(final String colour, final int increment) {
        int index = IntStream.range(0, COLOURS.length).filter(i -> colour.equals(COLOURS[i])).findFirst().orElse(-1);
        index += increment;
        if (increment > 0 && index >= COLOURS.length) index = 0;
        else if (increment < 0 && index < 0) index = COLOURS.length - 1;
        return COLOURS[index];
    }
}
