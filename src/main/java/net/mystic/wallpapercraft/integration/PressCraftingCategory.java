package net.mystic.wallpapercraft.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.registries.ForgeRegistries;

import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.ModBlocks;
import net.mystic.wallpapercraft.recipes.PressCraftingRecipe;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record PressCraftingCategory(PressCraftingRecipe recipe) implements ICraftingCategoryExtension {
    private static final int SLOT_X0 = 1;
    private static final int SLOT_X1 = 19;
    private static final int SLOT_Y0 = 1;
    private static final int SLOT_Y1 = 19;

    private static final int OUT_X = 94;
    private static final int OUT_Y = 19;

    // JEI 11 width/height for a 2×2 grid
    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 2;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper grid, IFocusGroup focuses) {
        // Optional fallback base if helpers can’t resolve a real base for the output
        Item fallbackBaseItem = ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("solidgray-0"));

        // Build all possible outputs (used when not focused)
        List<ItemStack> outputs = ModBlocks.BLOCKS.values().stream()
                .map(b -> {
                    ResourceLocation id = b.getId();
                    Item it = ForgeRegistries.ITEMS.getValue(id);
                    return it == null ? ItemStack.EMPTY : new ItemStack(it);
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Try to read an OUTPUT focus (what the user hovered/clicked in JEI)
        Optional<ItemStack> focusedOutput = focuses
                .getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT)
                .findFirst()
                .map(IFocus::getTypedValue)
                .map(ITypedIngredient::getIngredient);

        if (focusedOutput.isPresent()) {
            // Show the exact inputs for THIS output only (0–1 each).
            ItemStack out = focusedOutput.get();

            // Base decorative (should be the one that differs by exactly one attribute)
            Optional<ItemStack> baseOpt = recipe.getBaseForOutput(out);
            if (baseOpt.isPresent()) {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0).addItemStack(baseOpt.get());
            } else if (fallbackBaseItem != null) {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0).addItemStack(new ItemStack(fallbackBaseItem));
            } else {
                // even if we can’t resolve a base, keep the grid shape consistent
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0);
            }

            // Conditionally add the three “optional” ingredients (0–1 each)
            recipe.getPressForOutput(out)
                    .ifPresent(st -> builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X1, SLOT_Y0).addItemStack(st));

            recipe.getColourForOutput(out)
                    .ifPresent(st -> builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y1).addItemStack(st));

            recipe.getVariantForOutput(out)
                    .ifPresent(st -> builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X1, SLOT_Y1).addItemStack(st));

            // Focused output only
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y).addItemStack(out);
            return;
        }

        // No focus: pick an example output and show a representative base + its single needed press (if any)
        ItemStack example = outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0);

        if (!example.isEmpty()) {
            // Base for example output
            Optional<ItemStack> baseOpt = recipe.getBaseForOutput(example);
            if (baseOpt.isPresent()) {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0).addItemStack(baseOpt.get());
            } else if (fallbackBaseItem != null) {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0).addItemStack(new ItemStack(fallbackBaseItem));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0);
            }

            // At most one of these will be present for the example output
            recipe.getPressForOutput(example)
                    .ifPresent(st -> builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X1, SLOT_Y0).addItemStack(st));
            recipe.getColourForOutput(example)
                    .ifPresent(st -> builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y1).addItemStack(st));
            recipe.getVariantForOutput(example)
                    .ifPresent(st -> builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X1, SLOT_Y1).addItemStack(st));

        } else {
            // With no example, still render the 2×2 input shape
            if (fallbackBaseItem != null) {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0).addItemStack(new ItemStack(fallbackBaseItem));
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y0);
            }
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X1, SLOT_Y0);
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X0, SLOT_Y1);
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X1, SLOT_Y1);
        }

        // Show all craftable outputs when not focused
        if (!outputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y).addItemStacks(outputs);
        }
    }
}
