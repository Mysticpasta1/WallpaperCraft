package net.mystic.wallpapercraft.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.mystic.wallpapercraft.Wallpapercraft;

public final class Tags {

    public static final TagKey<Item> PRESS_PATTERN = tag("press_pattern");
    public static final TagKey<Item> PRESS_COLOUR  = tag("press_colour");
    public static final TagKey<Item> PRESS_VARIANT = tag("press_variant");
    // public static final TagKey<Item> DECORATIVE_ITEM = tag("decorative_item");

    private Tags() {}

    private static TagKey<Item> tag(String path) {
        return ItemTags.create(Wallpapercraft.getId(path)); // RL = wallpapercraft:path
    }
}
