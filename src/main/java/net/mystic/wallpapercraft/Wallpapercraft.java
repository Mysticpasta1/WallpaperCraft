package net.mystic.wallpapercraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.mystic.wallpapercraft.network.Network;
import net.mystic.wallpapercraft.util.ModRecipeSerializers;

@Mod(Wallpapercraft.MODID)
public class Wallpapercraft {

    public static final String MODID = "wallpapercraft";
    public static final CreativeModeTab TAB = new CreativeTab();

    public Wallpapercraft() {
        MinecraftForge.EVENT_BUS.register(this);
        DeferredRegistries.setup();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    @Mod.EventBusSubscriber(modid = Wallpapercraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusHandlers {
        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
                event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
                    helper.register(Wallpapercraft.getId("presscrafting"),
                            ModRecipeSerializers.PRESSCRAFTING);
                });
            }
        }
    }

    public static ResourceLocation getId(final String path) {
        return new ResourceLocation(MODID, path);
    }

    public static ResourceLocation getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty()) return getId(path);
        return new ResourceLocation(namespace, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        Network.init();
    }
}
