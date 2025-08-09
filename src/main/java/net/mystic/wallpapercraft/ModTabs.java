package net.mystic.wallpapercraft;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.registries.RegistryObject;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModTabs {
    public static CreativeModeTab WALLPAPER_TAB;

    public static final List<Supplier<? extends ItemLike>> MAIN_BLOCKS = new ArrayList<>();
    public static final List<Supplier<? extends ItemLike>> MAIN_ITEMS = new ArrayList<>();

    // 1) create the tab
    @SubscribeEvent
    public static void onRegisterTabs(CreativeModeTabEvent.Register event) {
        WALLPAPER_TAB = event.registerCreativeModeTab(
                new ResourceLocation(Wallpapercraft.MODID, "main"),
                builder -> builder
                        .title(Component.translatable("itemGroup.wallpapercraft"))
                        .icon(() -> {
                            var item = ForgeRegistries.ITEMS.getValue(
                                    new ResourceLocation(Wallpapercraft.MODID, "pressstonebrick"));
                            return item == null ? ItemStack.EMPTY : item.getDefaultInstance();
                        })
        );
    }

    @SubscribeEvent
    public static void onBuildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == WALLPAPER_TAB) {
            MAIN_BLOCKS.forEach(itemLike -> event.accept(itemLike.get()));
            MAIN_ITEMS.forEach(itemLike -> event.accept(itemLike.get()));
        }
    }

    public static <T extends Item> RegistryObject<T> addToMainTab (RegistryObject<T> itemLike) {
        MAIN_BLOCKS.add(itemLike);
        return itemLike;
    }

    public static <T extends Item> RegistryObject<T> addToMainTabItems (RegistryObject<T> itemLike) {
        MAIN_ITEMS.add(itemLike);
        return itemLike;
    }
}
