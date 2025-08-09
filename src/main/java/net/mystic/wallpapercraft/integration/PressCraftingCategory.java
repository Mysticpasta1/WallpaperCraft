package net.mystic.wallpapercraft.integration;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;
import net.mystic.wallpapercraft.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PressCraftingCategory implements ICraftingCategoryExtension {
    private final PressCraftingRecipe recipe;

    public PressCraftingCategory(PressCraftingRecipe recipe) {
        this.recipe = recipe;
    }

    // Width/height belong to ICraftingCategoryExtension in JEI 11
    @Override public int getWidth()  { return 2; }
    @Override public int getHeight() { return 2; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper grid, IFocusGroup focuses) {
        var baseItem = ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("solidgray-0"));
        List<ItemStack> base = (baseItem == null) ? List.of() : List.of(new ItemStack(baseItem));

        List<ItemStack> pressPattern = stacksFromTag(Tags.PRESS_PATTERN);
        List<ItemStack> pressColour  = stacksFromTag(Tags.PRESS_COLOUR);
        List<ItemStack> pressVariant = stacksFromTag(Tags.PRESS_VARIANT);

        List<ItemStack> outputs = ModBlocks.BLOCKS.values().stream()
                .map(b -> {
                    ResourceLocation id = Wallpapercraft.getId(b.getNameForRegistry());
                    var it = ForgeRegistries.ITEMS.getValue(id);
                    return it == null ? ItemStack.EMPTY : new ItemStack(it);
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // 2×2 inputs
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStacks(base);
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addItemStacks(pressPattern);
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addItemStacks(pressColour);
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 19).addItemStacks(pressVariant);

        // Output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 19).addItemStacks(outputs);
    }

    private static List<ItemStack> stacksFromTag(TagKey<Item> tag) {
        ITagManager<Item> tm = ForgeRegistries.ITEMS.tags(); // no Optional in your env
        if (tm == null) return List.of();
        List<ItemStack> out = new ArrayList<>();
        tm.getTag(tag).forEach(i -> out.add(new ItemStack(i)));
        return out;
    }
}
