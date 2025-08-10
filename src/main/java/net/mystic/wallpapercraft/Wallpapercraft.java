package net.mystic.wallpapercraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.items.ModItems;
import net.mystic.wallpapercraft.network.Network;
import net.mystic.wallpapercraft.sounds.SoundInit;
import net.mystic.wallpapercraft.recipes.ModRecipeSerializers;

@Mod(Wallpapercraft.MODID)
public class Wallpapercraft {
    public static final String MODID = "wallpapercraft";

    public Wallpapercraft() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        SoundInit.setup();
        bus.addListener(this::setup);
        ModBlocks.register(bus);
        ModItems.register(bus);
        ModBlocks.bootstrap();
        ModItems.bootstrap();
        ModTabs.register(bus);
        ModRecipeSerializers.register(bus);
    }

    public static ResourceLocation getId(final String path) {
        return new ResourceLocation(MODID, path);
    }

    public static ResourceLocation getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty()) return getId(path);
        return new ResourceLocation(namespace, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Network::init);
    }
}
