package net.mystic.wallpapercraft.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressVariant;
import net.mystic.wallpapercraft.sounds.ModSoundType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecorativeBlockGlass extends AbstractGlassBlock implements IDecorativeBlock {

    private static final String POSTFIX = "";

    private final String pattern;
    private final String colour;
    private final String suffix;

    public DecorativeBlockGlass(final String pattern,
                                final String colour,
                                final int suffix,
                                final BlockBehaviour.Properties props,
                                final int light) {
        super(props.lightLevel(s -> light));
        this.pattern = pattern;
        this.colour = colour;
        this.suffix = "-" + suffix;
    }

    @Override
    public void attack(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull Player player) {
        IDecorativeBlock.super.onBlockClicked(state, level, pos, player);
    }

    @Override
    public String getPostfix() {
        return POSTFIX;
    }

    @Override
    public String getNameForRegistry() {
        return pattern + colour + suffix + POSTFIX;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public String getColour() {
        return colour;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public SoundType getSoundType(final BlockState state,
                                  final LevelReader world,
                                  final BlockPos pos,
                                  @Nullable final Entity entity) {
        if (!(entity instanceof Player player)) return SoundType.GLASS;

        var held = player.getMainHandItem();
        if (held.isEmpty()) return SoundType.GLASS;

        var paintbrush = BuiltInRegistries.ITEM.get(Wallpapercraft.getId("paintbrush"));
        if (held.getItem() == paintbrush || held.getItem() instanceof PressColour || held.getItem() instanceof PressVariant) {
            return ModSoundType.BLOCK_CHANGE;
        }

        return SoundType.STONE;
    }
}
