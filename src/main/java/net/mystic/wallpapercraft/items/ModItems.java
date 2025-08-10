package net.mystic.wallpapercraft.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.mystic.wallpapercraft.ModTabs;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;

import java.util.HashMap;
import java.util.Map;

public final class ModItems {

    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, Wallpapercraft.MODID);

    public static final Map<String, DeferredHolder<Item, ? extends Item>> ITEM_ROS = new HashMap<>();

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
            DeferredHolder<Block, ? extends Block> blockHolder = e.getValue();

            DeferredHolder<Item, DecorativeItem> itemHolder = ITEMS.register(name,
                    () -> new DecorativeItem(blockHolder.get(), new Item.Properties()));

            ITEM_ROS.put(name, itemHolder);
            ModTabs.addToMainTab(itemHolder);
        }
    }

    private static void registerPressPatterns() {
        for (String p : ModBlocks.PATTERNS) {
            String name = "press" + p.toLowerCase();
            DeferredHolder<Item, PressPattern> holder = ITEMS.register(name, () -> new PressPattern(p));
            ITEM_ROS.put(name, holder);
            ModTabs.addToMainTabItems(holder);
        }
    }

    private static void registerPressColours() {
        for (String c : ModBlocks.COLOURS) {
            String name = "press" + c.toLowerCase();
            DeferredHolder<Item, PressColour> holder = ITEMS.register(name, () -> new PressColour(c));
            ITEM_ROS.put(name, holder);
            ModTabs.addToMainTabItems(holder);
        }
    }

    private static void registerPressVariants() {
        for (int i = 0; i <= 14; i++) {
            String name = "pressvariant" + i;
            int finalI = i;
            DeferredHolder<Item, PressVariant> holder = ITEMS.register(name, () -> new PressVariant(Integer.toString(finalI)));
            ITEM_ROS.put(name, holder);
            ModTabs.addToMainTabItems(holder);
        }
    }

    private static void registerSimple(String name, Item.Properties props) {
        DeferredHolder<Item, ? extends Item> holder = ITEMS.register(name, () -> new Item(props));
        ITEM_ROS.put(name, holder);
        ModTabs.addToMainTabItems(holder);
    }

    public static DecorativeItem get(final String pattern, final String colour, final int suffix, final String postfix) {
        ResourceLocation id = Wallpapercraft.getId(pattern + colour + "-" + suffix + postfix);
        Item item = BuiltInRegistries.ITEM.get(id);
        return item instanceof DecorativeItem di ? di : null;
    }

    public static DecorativeItem get(final ResourceLocation location) {
        Item item = BuiltInRegistries.ITEM.get(location);
        return item instanceof DecorativeItem di ? di : null;
    }
}
