package net.mystic.wallpapercraft.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class JustEnoughItems implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = Wallpapercraft.getId("plugin/main");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IExtendableRecipeCategory<CraftingRecipe, ICraftingCategoryExtension> cat = registration.getCraftingCategory();
        cat.addCategoryExtension(PressCraftingRecipe.class, PressCraftingCategory::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Crafting recipes
        List<CraftingRecipe> recipes = getCraftingRecipes().stream()
                .filter(r -> r instanceof PressCraftingRecipe)
                .collect(Collectors.toList());
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);

        // Info pages
        addInfoPage(registration, new ItemStack(ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("solidgray-0"))));
        addInfoPage(registration, new ItemStack(ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("paintbrush"))));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        List<ItemStack> removals = new ArrayList<>();
        removals.add(new ItemStack(ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("pressstamp"))));
        removals.add(new ItemStack(ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("pressjewel"))));
        jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, removals);
    }

    // ---- helpers ----

    private static List<CraftingRecipe> getCraftingRecipes() {
        // Client-side only; safe during JEI registration
        return Minecraft.getInstance().level.getRecipeManager().getRecipes().stream()
                .filter(r -> r instanceof CraftingRecipe)
                .map(r -> (CraftingRecipe) r)
                .collect(Collectors.toList());
    }

    private static void addInfoPage(IRecipeRegistration reg, ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() == null) return;
        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) return;
        // lang key: jei.<namespace>.<path>.desc
        String descKey = "jei." + key.getNamespace() + "." + key.getPath() + ".desc";
        reg.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, Component.translatable(descKey));
    }
}
