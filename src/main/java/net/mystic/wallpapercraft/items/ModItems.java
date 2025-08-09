package net.mystic.wallpapercraft.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import net.mystic.wallpapercraft.ModTabs;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;

@Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    @SubscribeEvent
    public static void onRegisterItems(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) return;

        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            // Decorative BlockItems (likely extend BlockItem) -> show in MAIN_BLOCKS
            ModBlocks.BLOCKS.keySet().stream().sorted().forEachOrdered(name -> {
                ResourceLocation id = Wallpapercraft.getId(name);
                Block block = ForgeRegistries.BLOCKS.getValue(id);
                if (block != null) {
                    helper.register(id, new DecorativeItem(block, new Item.Properties()));
                    ModTabs.MAIN_BLOCKS.add(() -> ForgeRegistries.ITEMS.getValue(id));
                }
            });

            // Press: patterns
            for (String s : ModBlocks.PATTERNS) {
                ResourceLocation id = Wallpapercraft.getId("press" + s.toLowerCase());
                helper.register(id, new PressPattern(s));
                ModTabs.MAIN_ITEMS.add(() -> ForgeRegistries.ITEMS.getValue(id));
            }

            // Press: colours
            for (String s : ModBlocks.COLOURS) {
                ResourceLocation id = Wallpapercraft.getId("press" + s.toLowerCase());
                helper.register(id, new PressColour(s));
                ModTabs.MAIN_ITEMS.add(() -> ForgeRegistries.ITEMS.getValue(id));
            }

            // Press: numeric variants
            for (int i = 0; i <= 14; i++) {
                ResourceLocation id = Wallpapercraft.getId("pressvariant" + i);
                helper.register(id, new PressVariant(Integer.toString(i)));
                ModTabs.MAIN_ITEMS.add(() -> ForgeRegistries.ITEMS.getValue(id));
            }

            // Simple items
            registerSimpleItem(helper, "pressblank", 64);
            registerSimpleItem(helper, "paintbrush", 1);

            // BlockItems for specific blocks (show as blocks)
            registerBlockItem(helper, "compressed");
            registerBlockItem(helper, "hardened");
        });
    }

    private static void registerSimpleItem(RegisterEvent.RegisterHelper<Item> helper, String name, int stack) {
        ResourceLocation id = Wallpapercraft.getId(name);
        helper.register(id, new Item(new Item.Properties().stacksTo(stack)));
        ModTabs.MAIN_ITEMS.add(() -> ForgeRegistries.ITEMS.getValue(id));
    }

    private static void registerBlockItem(RegisterEvent.RegisterHelper<Item> helper, String name) {
        ResourceLocation id = Wallpapercraft.getId(name);
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        if (block != null) {
            helper.register(id, new BlockItem(block, new Item.Properties().stacksTo(64)));
            ModTabs.MAIN_BLOCKS.add(() -> ForgeRegistries.ITEMS.getValue(id));
        }
    }

    // (unchanged helpers)
    public static DecorativeItem get(final String pattern, final String colour, final int suffix, final String postfix) {
        return (DecorativeItem) ForgeRegistries.ITEMS.getValue(
                Wallpapercraft.getId(pattern + colour + "-" + suffix + postfix)
        );
    }

    public static DecorativeItem get(final ResourceLocation location) {
        return (DecorativeItem) ForgeRegistries.ITEMS.getValue(location);
    }
}
