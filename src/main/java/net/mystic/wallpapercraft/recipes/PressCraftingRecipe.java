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
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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

import java.util.Objects;
import java.util.Optional;

public final class PressCraftingRecipe implements CraftingRecipe {
    public PressCraftingRecipe() {
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.BUILDING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PRESSCRAFTING.get();
    }

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
            colour = deco.getColour();
            suffix = deco.getSuffix();
            if (suffix.startsWith("-")) suffix = suffix.substring(1); // normalize
            postfix = deco.getPostfix();
            break;
        }

        // apply exactly one press
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
                    String v = press.getVariant();
                    if (v.startsWith("-")) v = v.substring(1);
                    suffix = v;
                    changed = true;
                }
                default -> {
                }
            }
        }

        if (!changed) return ItemStack.EMPTY;

        String sep = suffix.isEmpty() ? "" : "-";
        String targetPath = pattern + colour + sep + suffix + postfix;

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

        @Override
        public @NotNull MapCodec<PressCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PressCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public Optional<ItemStack> getBaseForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        Optional<ItemStack> byVariant = findDecorativeBase(out.namespace, out.pattern, out.colour, null, out.postfix);
        if (byVariant.isPresent()) return byVariant;

        Optional<ItemStack> byColour = findDecorativeBase(out.namespace, out.pattern, null, out.suffix, out.postfix);
        if (byColour.isPresent()) return byColour;

        return findDecorativeBase(out.namespace, null, out.colour, out.suffix, out.postfix);
    }

    public Optional<ItemStack> getPressForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        Optional<ItemStack> base = findDecorativeBase(out.namespace, /*pattern*/null, out.colour, out.suffix, out.postfix);
        return base.isPresent() ? findPressItem(PressPattern.class, out.pattern) : Optional.empty();
    }

    public Optional<ItemStack> getColourForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        Optional<ItemStack> base = findDecorativeBase(out.namespace, out.pattern, /*colour*/null, out.suffix, out.postfix);
        return base.isPresent() ? findPressItem(PressColour.class, out.colour) : Optional.empty();
    }

    public Optional<ItemStack> getVariantForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        Optional<ItemStack> base = findDecorativeBase(out.namespace, out.pattern, out.colour, /*suffix*/null, out.postfix);
        return base.isPresent() ? findPressItem(PressVariant.class, out.suffix) : Optional.empty();
    }

    private Optional<ItemStack> findDecorativeBase(String namespace, String pattern, String colour, String suffix, String postfix) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof DecorativeItem decoItem)) continue;
            Block b = ((BlockItem) item).getBlock();
            if (!(b instanceof IDecorativeBlock db)) continue;

            ResourceLocation key = BuiltInRegistries.BLOCK.getKey(b);
            if (!Objects.equals(key.getNamespace(), namespace)) continue;

            if (!Objects.equals(db.getPostfix(), postfix)) continue;

            String p = db.getPattern();
            String c = db.getColour();
            String s = db.getSuffix();
            if (s != null && s.startsWith("-")) s = s.substring(1);

            if ((pattern == null || pattern.equals(p)) &&
                    (colour == null || colour.equals(c)) &&
                    (suffix == null || suffix.equals(s))) {
                return Optional.of(new ItemStack(item));
            }
        }
        return Optional.empty();
    }

    private Optional<ItemStack> findPressItem(Class<? extends Press> cls, String desiredVariant) {
        String want = desiredVariant == null ? "" : desiredVariant;
        if (want.startsWith("-")) want = want.substring(1);

        for (Item item : BuiltInRegistries.ITEM) {
            if (!cls.isInstance(item)) continue;
            Press press = (Press) item;
            String v = press.getVariant();
            if (v != null && v.startsWith("-")) v = v.substring(1);
            if (Objects.equals(v, want)) {
                return Optional.of(new ItemStack(item));
            }
        }
        return Optional.empty();
    }

    private record DecorativeInfo(String namespace, String pattern, String colour, String suffix, String postfix) {

        static Optional<DecorativeInfo> from(ItemStack stack) {
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bi)) return Optional.empty();
            Block b = bi.getBlock();
            if (!(b instanceof IDecorativeBlock db)) return Optional.empty();
            ResourceLocation key = BuiltInRegistries.BLOCK.getKey(b);

            String s = db.getSuffix();
            if (s != null && s.startsWith("-")) s = s.substring(1);

            return Optional.of(new DecorativeInfo(
                    key.getNamespace(),
                    db.getPattern(),
                    db.getColour(),
                    s == null ? "" : s,
                    db.getPostfix()
            ));
        }
    }
}
