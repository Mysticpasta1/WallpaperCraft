package net.mystic.wallpapercraft.util;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;

// somewhere accessible (e.g., ModRecipeSerializers or inside PressCraftingRecipe)
public final class ModRecipeSerializers {
    public static final RecipeSerializer<PressCraftingRecipe> PRESSCRAFTING =
        new PressCraftingRecipe.Serializer();
}
