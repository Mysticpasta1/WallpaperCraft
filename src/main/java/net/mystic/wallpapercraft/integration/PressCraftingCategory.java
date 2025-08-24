package net.mystic.wallpapercraft.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class PressCraftingCategory implements ICraftingCategoryExtension<PressCraftingRecipe> {

    private static IDrawableStatic SLOT_BG;

    public static void setSlotBg(IDrawableStatic slotBg) {
        SLOT_BG = slotBg;
    }

    @Override
    public int getWidth(@NotNull RecipeHolder<PressCraftingRecipe> r) { return 2; }

    @Override
    public int getHeight(@NotNull RecipeHolder<PressCraftingRecipe> r) { return 2; }

    @Override
    public void setRecipe(@NotNull RecipeHolder<PressCraftingRecipe> r,
                          @NotNull IRecipeLayoutBuilder builder,
                          @NotNull ICraftingGridHelper grid,
                          @NotNull IFocusGroup focuses) {

        PressCraftingRecipe recipe = r.value();

        List<ItemStack> outputs = ModBlocks.BLOCKS.values().stream()
                .map(ro -> ro.get().asItem())
                .filter(it -> it != net.minecraft.world.item.Items.AIR)
                .map(ItemStack::new)
                .toList();

        ItemStack fallbackBase = stackOf(Wallpapercraft.getId("solidgray-0"));

        Optional<ItemStack> focusedOutput = focuses
                .getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT)
                .findFirst()
                .map(IFocus::getTypedValue)
                .map(ITypedIngredient::getIngredient);

        final int X = 10, Y = 10, S = 19;
        final int OUT_X = 94, OUT_Y = 19;

        if (focusedOutput.isPresent()) {
            ItemStack out = focusedOutput.get();

            Optional<ItemStack> baseOpt = recipe.getBaseForOutput(out);
            if (baseOpt.isPresent()) {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .addItemStack(baseOpt.get())
                        .setBackground(SLOT_BG, -1, -1);
            } else if (!fallbackBase.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .addItemStack(fallbackBase)
                        .setBackground(SLOT_BG, -1, -1);
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .setBackground(SLOT_BG, -1, -1);
            }

            recipe.getPressForOutput(out).ifPresent(st ->
                    builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y)
                            .addItemStack(st)
                            .setBackground(SLOT_BG, -1, -1)
            );

            recipe.getColourForOutput(out).ifPresent(st ->
                    builder.addSlot(RecipeIngredientRole.INPUT, X, Y + S)
                            .addItemStack(st)
                            .setBackground(SLOT_BG, -1, -1)
            );

            recipe.getVariantForOutput(out).ifPresent(st ->
                    builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y + S)
                            .addItemStack(st)
                            .setBackground(SLOT_BG, -1, -1)
            );

            builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                    .addItemStack(out)
                    .setBackground(SLOT_BG, -1, -1);

            return;
        }

        ItemStack example = outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst();

        if (!example.isEmpty()) {
            Optional<ItemStack> baseOpt = recipe.getBaseForOutput(example);
            if (baseOpt.isPresent()) {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .addItemStack(baseOpt.get())
                        .setBackground(SLOT_BG, -1, -1);
            } else if (!fallbackBase.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .addItemStack(fallbackBase)
                        .setBackground(SLOT_BG, -1, -1);
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .setBackground(SLOT_BG, -1, -1);
            }

            recipe.getPressForOutput(example).ifPresent(st ->
                    builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y)
                            .addItemStack(st)
                            .setBackground(SLOT_BG, -1, -1)
            );
            recipe.getColourForOutput(example).ifPresent(st ->
                    builder.addSlot(RecipeIngredientRole.INPUT, X, Y + S)
                            .addItemStack(st)
                            .setBackground(SLOT_BG, -1, -1)
            );
            recipe.getVariantForOutput(example).ifPresent(st ->
                    builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y + S)
                            .addItemStack(st)
                            .setBackground(SLOT_BG, -1, -1)
            );
        } else {
            if (!fallbackBase.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .addItemStack(fallbackBase)
                        .setBackground(SLOT_BG, -1, -1);
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, X, Y)
                        .setBackground(SLOT_BG, -1, -1);
            }
            builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y).setBackground(SLOT_BG, -1, -1);
            builder.addSlot(RecipeIngredientRole.INPUT, X, Y + S).setBackground(SLOT_BG, -1, -1);
            builder.addSlot(RecipeIngredientRole.INPUT, X + S, Y + S).setBackground(SLOT_BG, -1, -1);
        }

        if (!outputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                    .addItemStacks(outputs)
                    .setBackground(SLOT_BG, -1, -1);
        }
    }

    private static ItemStack stackOf(ResourceLocation id) {
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}
