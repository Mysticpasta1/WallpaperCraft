package net.mystic.wallpapercraft.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.blocks.IDecorativeBlock;
import net.mystic.wallpapercraft.items.DecorativeItem;
import net.mystic.wallpapercraft.items.Press;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressPattern;
import net.mystic.wallpapercraft.items.PressVariant;
import org.jetbrains.annotations.NotNull;

public final class PressCraftingRecipe implements CraftingRecipe {
    @Override public @NotNull RecipeType<?> getType() { return RecipeType.CRAFTING; }
    @Override public @NotNull CraftingBookCategory category() { return CraftingBookCategory.BUILDING; }
    @Override public boolean isSpecial() { return true; } // hide from recipe book
    @Override public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) { return ItemStack.EMPTY; }
    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public @NotNull RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.PRESSCRAFTING.get(); }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        int patternPress = 0, colourPress = 0, variantPress = 0, decorative = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            Item it = stack.getItem();
            switch (it) {
                case PressPattern ignored -> patternPress++;
                case PressColour ignored -> colourPress++;
                case PressVariant ignored -> variantPress++;
                default -> {
                    if (!(it instanceof DecorativeItem)) return false;
                    decorative++;
                }
            }

            if (decorative > 1 || patternPress > 1 || colourPress > 1 || variantPress > 1) return false;
        }

        return decorative == 1 && (patternPress == 1 || colourPress == 1 || variantPress == 1);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider provider) {
        String pattern = "", colour = "", suffix = "", postfix = "";
        String sourceNamespace = "";
        boolean changed = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof DecorativeItem)) continue;

            Block block = ((BlockItem) stack.getItem()).getBlock();
            if (!(block instanceof IDecorativeBlock deco)) return ItemStack.EMPTY;

            ResourceLocation blkKey = BuiltInRegistries.BLOCK.getKey(block);
            sourceNamespace = blkKey.getNamespace();

            pattern = deco.getPattern();
            colour  = deco.getColour();
            suffix  = deco.getSuffix();
            if (suffix.startsWith("-")) suffix = suffix.substring(1);
            postfix = deco.getPostfix();
            break;
        }

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof Press press)) continue;

            switch (press) {
                case PressPattern ignored -> {
                    pattern = press.getVariant();
                    changed = true;
                }
                case PressColour ignored -> {
                    colour = press.getVariant();
                    changed = true;
                }
                case PressVariant ignored -> {
                    suffix = press.getVariant();
                    changed = true;
                }
                default -> {
                }
            }
        }

        if (!changed) return ItemStack.EMPTY;

        String targetPath = pattern + colour + "-" + suffix + postfix;
        return BuiltInRegistries.ITEM
                .getOptional(Wallpapercraft.getId(sourceNamespace, targetPath))
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> list = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof Press) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                list.set(i, copy);
            }
        }
        return list;
    }

    public static final class Serializer implements RecipeSerializer<PressCraftingRecipe> {
        private static final PressCraftingRecipe INSTANCE = new PressCraftingRecipe();
        private static final MapCodec<PressCraftingRecipe> CODEC = MapCodec.unit(INSTANCE);
        private static final StreamCodec<RegistryFriendlyByteBuf, PressCraftingRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override public @NotNull MapCodec<PressCraftingRecipe> codec() { return CODEC; }
        @Override public @NotNull StreamCodec<RegistryFriendlyByteBuf, PressCraftingRecipe> streamCodec() { return STREAM_CODEC; }
    }

    public PressCraftingRecipe() {}
}
