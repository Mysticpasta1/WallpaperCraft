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
import java.util.Objects;
import java.util.Optional;

/**
 * Press crafting: one DecorativeItem + exactly one Press (pattern OR colour OR variant) -> new DecorativeItem
 */
public record PressCraftingRecipe(ResourceLocation id) implements CraftingRecipe {

    public static final ResourceLocation NAME = Wallpapercraft.getId("presscrafting");

    public static final RecipeType<PressCraftingRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return NAME.toString();
        }
    };

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

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
    public ItemStack assemble(CraftingContainer inv) {
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
            colour = decoBlock.getColour();
            suffix = decoBlock.getSuffix();
            // normalize base suffix: store just the number (strip leading '-')
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
                // normalize variant suffix exactly like the base (strip any leading '-')
                String v = press.getVariant();
                if (v.startsWith("-")) v = v.substring(1);
                suffix = v;
                hasChanged = true;
            }
        }

        if (!hasChanged) return ItemStack.EMPTY;

        // build path: add '-' only if suffix is non-empty to avoid double/stray dashes
        final String sep = suffix.isEmpty() ? "" : "-";
        final String targetPath = pattern + colour + sep + suffix + postfix;

        // look up namespaced item (namespace from the decorative base block)
        final Item out = ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId(sourceNamespace, targetPath));
        return out == null ? ItemStack.EMPTY : new ItemStack(out);
    }

    @Override
    public boolean canCraftInDimensions(final int w, final int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

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
    public boolean isSpecial() {
        return true;
    } // hide from recipe book

    @Nonnull
    @Override
    public String getGroup() {
        return Wallpapercraft.MODID;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PRESSCRAFTING;
    }

    /**
     * No ForgeRegistryEntry, no setRegistryName — register in your RegisterEvent handler.
     */
    public static final class Serializer implements RecipeSerializer<PressCraftingRecipe> {
        public Serializer() {
        }

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

    /* ==============================
       Helpers for JEI category view
       ============================== */

    /**
     * Pick a base DecorativeItem for this output that differs by exactly one attribute.
     */
    public Optional<ItemStack> getBaseForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        // Prefer a base that matches (pattern, colour) -> needs only VARIANT press
        Optional<ItemStack> byVariant = findDecorativeBase(out.namespace, out.pattern, out.colour, /*suffix*/null, out.postfix);
        if (byVariant.isPresent()) return byVariant;

        // Next: match (pattern, suffix) -> needs only COLOUR press
        Optional<ItemStack> byColour = findDecorativeBase(out.namespace, out.pattern, /*colour*/null, out.suffix, out.postfix);
        if (byColour.isPresent()) return byColour;

        // Finally: match (colour, suffix) -> needs only PATTERN press
        Optional<ItemStack> byPattern = findDecorativeBase(out.namespace, /*pattern*/null, out.colour, out.suffix, out.postfix);
        return byPattern;
    }

    /**
     * If the chosen base differs only by PATTERN, return the PressPattern that makes the output.
     */
    public Optional<ItemStack> getPressForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        // If there exists a base with same (colour, suffix), then we only need a pattern press.
        Optional<ItemStack> base = findDecorativeBase(out.namespace, /*pattern*/null, out.colour, out.suffix, out.postfix);
        return base.isPresent() ? findPressItem(PressPattern.class, out.pattern) : Optional.empty();
    }

    /**
     * If the chosen base differs only by COLOUR, return the PressColour that makes the output.
     */
    public Optional<ItemStack> getColourForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        // If there exists a base with same (pattern, suffix), then we only need a colour press.
        Optional<ItemStack> base = findDecorativeBase(out.namespace, out.pattern, /*colour*/null, out.suffix, out.postfix);
        return base.isPresent() ? findPressItem(PressColour.class, out.colour) : Optional.empty();
    }

    /**
     * If the chosen base differs only by VARIANT (suffix), return the PressVariant that makes the output.
     */
    public Optional<ItemStack> getVariantForOutput(ItemStack output) {
        DecorativeInfo out = DecorativeInfo.from(output).orElse(null);
        if (out == null) return Optional.empty();

        // If there exists a base with same (pattern, colour), then we only need a variant press.
        Optional<ItemStack> base = findDecorativeBase(out.namespace, out.pattern, out.colour, /*suffix*/null, out.postfix);
        return base.isPresent() ? findPressItem(PressVariant.class, out.suffix) : Optional.empty();
    }

    /* ---------- internal lookups ---------- */

    private Optional<ItemStack> findDecorativeBase(String namespace, String pattern, String colour, String suffix, String postfix) {
        // pattern/colour/suffix can be null => "don't care"; postfix must match to ensure we stay within the same family.
        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof DecorativeItem decoItem)) continue;
            Block b = ((BlockItem) item).getBlock();
            if (!(b instanceof IDecorativeBlock db)) continue;

            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(b);
            if (key == null || !Objects.equals(key.getNamespace(), namespace)) continue;

            // postfix must match exactly
            if (!Objects.equals(db.getPostfix(), postfix)) continue;

            String p = db.getPattern();
            String c = db.getColour();
            String s = db.getSuffix();
            if (s != null && s.startsWith("-")) s = s.substring(1); // normalize like assemble()

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
        if (want.startsWith("-")) want = want.substring(1); // normalize

        for (Item item : ForgeRegistries.ITEMS) {
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

    /* ---------- tiny value object to read decorative info from an ItemStack ---------- */

    private static final class DecorativeInfo {
        final String namespace;
        final String pattern;
        final String colour;
        final String suffix;   // normalized: no leading '-'
        final String postfix;

        private DecorativeInfo(String ns, String p, String c, String s, String post) {
            this.namespace = ns;
            this.pattern = p;
            this.colour = c;
            this.suffix = s;
            this.postfix = post;
        }

        static Optional<DecorativeInfo> from(ItemStack stack) {
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bi)) return Optional.empty();
            Block b = bi.getBlock();
            if (!(b instanceof IDecorativeBlock db)) return Optional.empty();
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(b);
            if (key == null) return Optional.empty();

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
