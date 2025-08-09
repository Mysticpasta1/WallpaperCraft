package net.mystic.wallpapercraft.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;

@Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    @SubscribeEvent
    public static void onRegisterItems(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) return;

        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            ModBlocks.BLOCKS.keySet().stream().sorted().forEachOrdered(name -> {
                ResourceLocation id = Wallpapercraft.getId(name);
                Block block = ForgeRegistries.BLOCKS.getValue(id);
                if (block != null) {
                    helper.register(id, new DecorativeItem(
                            block, new Item.Properties().tab(Wallpapercraft.TAB)
                    ));
                }
            });

            // Press: patterns
            for (String s : ModBlocks.PATTERNS) {
                helper.register(Wallpapercraft.getId("press" + s.toLowerCase()), new PressPattern(s));
            }

            // Press: colours
            for (String s : ModBlocks.COLOURS) {
                helper.register(Wallpapercraft.getId("press" + s.toLowerCase()), new PressColour(s));
            }

            // Press: numeric variants
            for (int i = 0; i <= 14; i++) {
                helper.register(Wallpapercraft.getId("pressvariant" + i), new PressVariant(Integer.toString(i)));
            }


            // 3) Simple items
            helper.register(Wallpapercraft.getId("pressblank"),
                    new Item(new Item.Properties().tab(Wallpapercraft.TAB).stacksTo(64)));

            helper.register(Wallpapercraft.getId("paintbrush"),
                    new Item(new Item.Properties().tab(Wallpapercraft.TAB).stacksTo(1)));

            // 4) BlockItems for specific blocks
            registerBlockItem(helper, "compressed");
            registerBlockItem(helper, "hardened");
        });
    }

    private static void registerBlockItem(RegisterEvent.RegisterHelper<Item> helper, String name) {
        ResourceLocation id = Wallpapercraft.getId(name);
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        if (block != null) {
            helper.register(id, new BlockItem(
                    block, new Item.Properties().tab(Wallpapercraft.TAB).stacksTo(64)
            ));
        }
    }

    // Helpers (unchanged logic)
    public static DecorativeItem get(final String pattern, final String colour, final int suffix, final String postfix) {
        return (DecorativeItem) ForgeRegistries.ITEMS.getValue(
                Wallpapercraft.getId(pattern + colour + "-" + suffix + postfix)
        );
    }

    public static DecorativeItem get(final ResourceLocation location) {
        return (DecorativeItem) ForgeRegistries.ITEMS.getValue(location);
    }
}
