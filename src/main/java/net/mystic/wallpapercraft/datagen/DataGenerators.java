package net.mystic.wallpapercraft.datagen;

import net.mystic.wallpapercraft.Wallpapercraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Wallpapercraft.MODID, value = Dist.CLIENT)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) {
        var gen = e.getGenerator();
        var efh = e.getExistingFileHelper();

        gen.addProvider(e.includeServer(), new WPTags(gen.getPackOutput(), e.getLookupProvider(), efh));
        gen.addProvider(e.includeClient(), new WPBlockStateProvider(gen.getPackOutput(), efh));
        gen.addProvider(e.includeClient(), new WPItemModelProvider(gen.getPackOutput(), efh));
    }
}
