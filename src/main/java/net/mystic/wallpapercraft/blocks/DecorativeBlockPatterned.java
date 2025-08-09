package net.mystic.wallpapercraft.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressVariant;
import net.mystic.wallpapercraft.sounds.ModSoundType;

import javax.annotation.Nullable;

public class DecorativeBlockPatterned extends HalfTransparentBlock implements IDecorativeBlock {

    private static final String POSTFIX = "";

    protected final String pattern;
    protected final String colour;
    protected final String suffix;

    public DecorativeBlockPatterned(final String pattern, final String colour, final int suffix,
                                    final Material material, final SoundType soundType,
                                    final float hardness, final int light) {
        super(Block.Properties.of(material)
                .sound(soundType)
                .strength(hardness)
                .lightLevel(l -> light)
        );
        this.pattern = pattern;
        this.colour = colour;
        this.suffix = "-" + suffix;
    }

    @Override
    public void attack(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        IDecorativeBlock.super.onBlockClicked(state, level, pos, player);
    }

    @Override
    public String getPostfix() { return POSTFIX; }

    @Override
    public String getNameForRegistry() { return this.pattern + this.colour + this.suffix + POSTFIX; }

    @Override
    public String getPattern() { return this.pattern; }

    @Override
    public String getColour() { return this.colour; }

    @Override
    public String getSuffix() { return this.suffix; }

    @Override
    public SoundType getSoundType(final BlockState state, final LevelReader world, final BlockPos pos, @Nullable final Entity entity) {
        final SoundType base = state.getSoundType();
        if (!(entity instanceof Player player)) return base;

        var held = player.getMainHandItem();
        if (held.isEmpty()) return base;

        // Look up paintbrush by ID (no @ObjectHolder / static field)
        var paintbrush = ForgeRegistries.ITEMS.getValue(Wallpapercraft.getId("paintbrush"));
        if (held.getItem() == paintbrush || held.getItem() instanceof PressColour || held.getItem() instanceof PressVariant) {
            return ModSoundType.BLOCK_CHANGE;
        }
        return base;
    }
}
