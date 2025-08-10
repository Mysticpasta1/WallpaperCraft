package net.mystic.wallpapercraft.integration;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;
import org.jetbrains.annotations.NotNull;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;

import java.util.List;

public class PressCraftingCategory implements ICraftingCategoryExtension<PressCraftingRecipe> {

    private static IDrawableStatic SLOT_BG;

    public static void setSlotBg(IDrawableStatic slotBg) {
        SLOT_BG = slotBg;
    }

    @Override
    public int getWidth(@NotNull RecipeHolder<PressCraftingRecipe> r) {
        return 2;
    }

    @Override
    public int getHeight(@NotNull RecipeHolder<PressCraftingRecipe> r) {
        return 2;
    }

    @Override
    public void setRecipe(@NotNull RecipeHolder<PressCraftingRecipe> r,
                          @NotNull IRecipeLayoutBuilder builder,
                          @NotNull ICraftingGridHelper grid,
                          @NotNull IFocusGroup focuses) {
        ItemStack base = stackOf(Wallpapercraft.getId("solidgray-0"));
        var pressPattern = stacksFromTag(net.mystic.wallpapercraft.tags.Tags.PRESS_PATTERN);
        var pressColour = stacksFromTag(net.mystic.wallpapercraft.tags.Tags.PRESS_COLOUR);
        var pressVariant = stacksFromTag(net.mystic.wallpapercraft.tags.Tags.PRESS_VARIANT);
        if (base.isEmpty() || pressPattern.isEmpty() || pressColour.isEmpty() || pressVariant.isEmpty()) return;

        var outputs = ModBlocks.BLOCKS.values().stream()
                .map(ro -> ro.get().asItem())
                .filter(it -> it != net.minecraft.world.item.Items.AIR)
                .map(ItemStack::new)
                .toList();

        final int X = 10, Y = 10, S = 19;

        builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                .addItemStack(base)
                .setBackground(SLOT_BG, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y)
                .addItemStacks(pressPattern)
                .setBackground(SLOT_BG, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, X, Y + S)
                .addItemStacks(pressColour)
                .setBackground(SLOT_BG, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y + S)
                .addItemStacks(pressVariant)
                .setBackground(SLOT_BG, -1, -1);

        if (!outputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 19)
                    .addItemStacks(outputs)
                    .setBackground(SLOT_BG, -1, -1);
        }
    }

    private static ItemStack stackOf(ResourceLocation id) {
        var item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static List<ItemStack> stacksFromTag(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getTag(tag)
                .map(s -> s.stream().map(h -> new ItemStack(h.value())).toList())
                .orElse(List.of());
    }
}

