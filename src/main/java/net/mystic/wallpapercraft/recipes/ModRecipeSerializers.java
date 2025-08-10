package net.mystic.wallpapercraft.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.mystic.wallpapercraft.Wallpapercraft;

public final class ModRecipeSerializers {
    private ModRecipeSerializers() {}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Wallpapercraft.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, PressCraftingRecipe.Serializer> PRESSCRAFTING =
            SERIALIZERS.register("press_crafting", PressCraftingRecipe.Serializer::new);

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}
