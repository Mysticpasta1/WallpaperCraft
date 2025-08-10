package net.mystic.wallpapercraft.items;

import net.minecraft.world.item.Item;
import net.mystic.wallpapercraft.Wallpapercraft;

public abstract class Press extends Item {
    private final String variant;

    public Press(String variant) {
        super(new Item.Properties()
                .stacksTo(1));

        this.variant = variant;
    }

    public String getVariant() {
        return this.variant;
    }
}
