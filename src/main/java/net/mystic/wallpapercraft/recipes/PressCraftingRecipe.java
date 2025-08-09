package net.mystic.wallpapercraft.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.items.DecorativeItem;
import net.mystic.wallpapercraft.items.Press;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressPattern;
import net.mystic.wallpapercraft.items.PressVariant;
import net.mystic.wallpapercraft.util.ModRecipeSerializers;

import javax.annotation.Nonnull;

public class PressCraftingRecipe implements CraftingRecipe {

    public static final ResourceLocation NAME = Wallpapercraft.getId("presscrafting");

    public static final RecipeType<PressCraftingRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override public String toString() { return NAME.toString(); }
    };

    private final ResourceLocation id;

    public PressCraftingRecipe(final ResourceLocation id) {
        this.id = id;
    }

    @Override
    public RecipeType<?> getType() { return RecipeType.CRAFTING; }

    @Override
    public boolean matches(@Nonnull final CraftingContainer inv, @Nonnull final Level level) {
        int patternPressCount = 0, colourPressCount = 0, variantPressCount = 0, decorativeCount = 0;

        for (int k = 0; k < inv.getContainerSize(); k++) {
            final ItemStack stack = inv.getItem(k);
            if (stack.isEmpty()) continue;

            final Item it = stack.getItem();
            if (it instanceof PressPattern) {
                patternPressCount++;
            } else if (it instanceof PressColour) {
                colourPressCount++;
            } else if (it instanceof PressVariant) {
                variantPressCount++;
            } else {
                if (!(it instanceof DecorativeItem)) return false;
                decorativeCount++;
            }
            if (decorativeCount > 1 || patternPressCount > 1 || colourPressCount > 1 || variantPressCount > 1) {
                return false;
            }
        }

        return decorativeCount == 1 && (patternPressCount == 1 || colourPressCount == 1 || variantPressCount == 1);
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull final CraftingContainer inv) {
        String pattern = "", colour = "", suffix = "", postfix = "";
        String sourceNamespace = "";
        boolean hasChanged = false;

        // find decorative base
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof DecorativeItem)) continue;

            final Block block = ((BlockItem) stack.getItem()).getBlock();
            if (!(block instanceof IDecorativeBlock decoBlock)) return ItemStack.EMPTY;

            final ResourceLocation blkKey = ForgeRegistries.BLOCKS.getKey(block);
            if (blkKey == null) return ItemStack.EMPTY;
            sourceNamespace = blkKey.getNamespace();

            pattern = decoBlock.getPattern();
            colour  = decoBlock.getColour();
            suffix  = decoBlock.getSuffix();
            // normalize: store just the number (strip leading '-')
            if (suffix.startsWith("-")) suffix = suffix.substring(1);
            postfix = decoBlock.getPostfix();
            break;
        }

        // apply one press
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof Press press)) continue;

            if (press instanceof PressPattern) {
                pattern = press.getVariant();
                hasChanged = true;
            } else if (press instanceof PressColour) {
                colour = press.getVariant();
                hasChanged = true;
            } else if (press instanceof PressVariant) {
                suffix = press.getVariant(); // number string
                hasChanged = true;
            }
        }

        if (!hasChanged) return ItemStack.EMPTY;

        // always add the dash here
        final String targetPath = pattern + colour + "-" + suffix + postfix;
        final Item out = ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId(sourceNamespace, targetPath));
        return out == null ? ItemStack.EMPTY : new ItemStack(out);
    }

    @Override
    public boolean canCraftInDimensions(final int w, final int h) { return true; }

    @Nonnull
    @Override
    public ItemStack getResultItem() { return ItemStack.EMPTY; } // dynamic

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer inv) {
        final NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof Press) {
                final ItemStack copy = stack.copy();
                copy.setCount(1);
                list.set(i, copy);
            }
        }
        return list;
    }

    @Override
    public boolean isSpecial() { return true; } // hide from recipe book

    @Nonnull
    @Override
    public String getGroup() { return Wallpapercraft.MODID; }

    @Nonnull
    @Override
    public ResourceLocation getId() { return this.id; }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.PRESSCRAFTING; }

    /** No ForgeRegistryEntry, no setRegistryName — register in your RegisterEvent handler. */
    public static final class Serializer implements RecipeSerializer<PressCraftingRecipe> {
        public Serializer() {}

        @Nonnull
        @Override
        public PressCraftingRecipe fromJson(@Nonnull final ResourceLocation recipeId, @Nonnull final JsonObject json) {
            return new PressCraftingRecipe(recipeId);
        }

        @Nonnull
        @Override
        public PressCraftingRecipe fromNetwork(@Nonnull final ResourceLocation recipeId, @Nonnull final FriendlyByteBuf buf) {
            return new PressCraftingRecipe(recipeId);
        }

        @Override
        public void toNetwork(@Nonnull final FriendlyByteBuf buf, @Nonnull final PressCraftingRecipe recipe) {
            // nothing to write; recipe is dynamic
        }
    }
}
