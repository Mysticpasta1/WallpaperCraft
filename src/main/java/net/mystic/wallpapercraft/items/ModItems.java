package net.mystic.wallpapercraft.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mystic.wallpapercraft.ModTabs;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.blocks.ModBlocks;

import java.util.HashMap;
import java.util.Map;

public final class ModItems {

    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Wallpapercraft.MODID);

    public static final Map<String, RegistryObject<Item>> ITEM_ROS = new HashMap<>();

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
    public static void bootstrap() {
        registerAllBlockItems();
        registerPressPatterns();
        registerPressColours();
        registerPressVariants();
        registerSimple("pressblank", new Item.Properties().stacksTo(64));
        registerSimple("paintbrush", new Item.Properties().stacksTo(1));
    }

    private static void registerAllBlockItems() {
        for (var e : ModBlocks.BLOCKS.entrySet()) {
            String name = e.getKey();
            RegistryObject<Block> blockRO = e.getValue();
            RegistryObject<Item> itemRO = ITEMS.register(name,
                    () -> new DecorativeItem(blockRO.get(), new Item.Properties()));

            ITEM_ROS.put(name, itemRO);
            ModTabs.addToMainTab(itemRO);
        }
    }


    private static void registerPressPatterns() {
        for (String p : ModBlocks.PATTERNS) {
            String name = "press" + p.toLowerCase();
            RegistryObject<Item> ro = ITEMS.register(name, () -> new PressPattern(p));
            ITEM_ROS.put(name, ro);
            ModTabs.addToMainTabItems(ro);
        }
    }

    private static void registerPressColours() {
        for (String c : ModBlocks.COLOURS) {
            String name = "press" + c.toLowerCase();
            RegistryObject<Item> ro = ITEMS.register(name, () -> new PressColour(c));
            ITEM_ROS.put(name, ro);
            ModTabs.addToMainTabItems(ro);
        }
    }

    private static void registerPressVariants() {
        for (int i = 0; i <= 14; i++) {
            String name = "pressvariant" + i;
            int finalI = i;
            RegistryObject<Item> ro = ITEMS.register(name, () -> new PressVariant(Integer.toString(finalI)));
            ITEM_ROS.put(name, ro);
            ModTabs.addToMainTabItems(ro);
        }
    }

    private static void registerSimple(String name, Item.Properties props) {
        RegistryObject<Item> ro = ITEMS.register(name, () -> new Item(props));
        ITEM_ROS.put(name, ro);
        ModTabs.addToMainTabItems(ro);
    }

    @SuppressWarnings("unused")
    private static void registerBlockItem(String blockName) {
        Block block = ModBlocks.BLOCKS.get(blockName).get();
        RegistryObject<Item> ro = ITEMS.register(blockName, () -> new BlockItem(block, new Item.Properties().stacksTo(64)));
        ITEM_ROS.put(blockName, ro);
        ModTabs.addToMainTab(ro);
    }

    public static DecorativeItem get(final String pattern, final String colour, final int suffix, final String postfix) {
        ResourceLocation id = Wallpapercraft.getId(pattern + colour + "-" + suffix + postfix);
        Item item = ForgeRegistries.ITEMS.getValue(id);
        return item instanceof DecorativeItem di ? di : null;
    }

    public static DecorativeItem get(final ResourceLocation location) {
        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item instanceof DecorativeItem di ? di : null;
    }
}
