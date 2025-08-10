package net.mystic.wallpapercraft.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mystic.wallpapercraft.Wallpapercraft;


public final class ModRecipeSerializers {
    private ModRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Wallpapercraft.MODID);

    public static final RegistryObject<RecipeSerializer<?>> PRESSCRAFTING =
            SERIALIZERS.register("presscrafting", PressCraftingRecipe.Serializer::new);

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}
