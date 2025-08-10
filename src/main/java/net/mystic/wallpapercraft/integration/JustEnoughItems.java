package net.mystic.wallpapercraft.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JustEnoughItems implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = Wallpapercraft.getId("plugin/main");

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper gh = reg.getJeiHelpers().getGuiHelper();
        IDrawableStatic slotBg = gh.getSlotDrawable();
        PressCraftingCategory.setSlotBg(slotBg);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        var cat = registration.getCraftingCategory();
        cat.addExtension(PressCraftingRecipe.class, new PressCraftingCategory());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        addInfoPage(registration, stackOf(Wallpapercraft.getId("solidgray-0")));
        addInfoPage(registration, stackOf(Wallpapercraft.getId("paintbrush")));
    }

    private static List<RecipeHolder<CraftingRecipe>> getPressRecipeHolders() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return List.of();
        return mc.level.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(h -> h.value() instanceof PressCraftingRecipe)
                .toList();
    }

    private static List<CraftingRecipe> getCraftingRecipes() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return List.of();
        List<RecipeHolder<CraftingRecipe>> holders =
                mc.level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        List<CraftingRecipe> all = holders.stream()
                .map(RecipeHolder::value)
                .toList();
        return all.stream()
                .filter(r -> r instanceof PressCraftingRecipe)
                .toList();
    }

    private static ItemStack stackOf(ResourceLocation id) {
        var item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static void addInfoPage(IRecipeRegistration reg, ItemStack stack) {
        if (stack.isEmpty()) return;

        var key = BuiltInRegistries.ITEM.getKey(stack.getItem());

        String descKey = "jei." + key.getNamespace() + "." + key.getPath() + ".desc";
        reg.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, Component.translatable(descKey));
    }
}
