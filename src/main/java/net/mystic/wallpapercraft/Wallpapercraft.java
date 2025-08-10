package net.mystic.wallpapercraft;

import net.minecraft.resources.ResourceLocation;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.items.ModItems;
import net.mystic.wallpapercraft.recipes.ModRecipeSerializers;
import net.mystic.wallpapercraft.sounds.ModSoundTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod(Wallpapercraft.MODID)
public class Wallpapercraft {
    public static final String MODID = "wallpapercraft";

    public Wallpapercraft(IEventBus modBus) {
        ModTabs.register(modBus);
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModRecipeSerializers.register(modBus);
        ModSoundTypes.register(modBus);
        ModBlocks.bootstrap();
        ModItems.bootstrap();
        ModTabs.bootstrap();
    }

    @SubscribeEvent
    public static void onServerAboutToStart(net.neoforged.neoforge.event.server.ServerAboutToStartEvent e) {
        long count = e.getServer().getRecipeManager()
                .getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING).stream()
                .filter(h -> h.value() instanceof net.mystic.wallpapercraft.recipes.PressCraftingRecipe)
                .count();
        System.out.println("[Wallpapercraft] PressCrafting recipes loaded: " + count);
    }


    public static ResourceLocation getId(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty()) return getId(path);
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
