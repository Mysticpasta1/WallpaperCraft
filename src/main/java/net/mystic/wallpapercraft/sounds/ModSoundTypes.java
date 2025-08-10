package net.mystic.wallpapercraft.sounds;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSoundTypes {
    public ModSoundTypes () {}

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Wallpapercraft.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHANGE_IN_WORLD =
            SOUND_EVENTS.register("block_change_in_world",
                    () -> SoundEvent.createFixedRangeEvent(Wallpapercraft.getId("block_change_in_world"), 1.0f));

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    public static final SoundType BLOCK_CHANGE = new DeferredSoundType(
            1.0F, 1.0F,
            () -> SoundEvents.STONE_BREAK,
            () -> SoundEvents.STONE_STEP,
            () -> SoundEvents.STONE_PLACE,
            ModSoundTypes.BLOCK_CHANGE_IN_WORLD,
            () -> SoundEvents.STONE_FALL
    );
}
