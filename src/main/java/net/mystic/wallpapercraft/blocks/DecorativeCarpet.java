package net.mystic.wallpapercraft.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mystic.wallpapercraft.Wallpapercraft;
import net.mystic.wallpapercraft.items.PressColour;
import net.mystic.wallpapercraft.items.PressVariant;
import net.mystic.wallpapercraft.sounds.ModSoundTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecorativeCarpet extends DecorativeBlockPatterned implements IDecorativeBlock {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final String POSTFIX = "_carpet";

    public DecorativeCarpet(final String pattern,
                            final String colour,
                            final int suffix,
                            final BlockBehaviour.Properties props) {
        super(pattern, colour, suffix, props, 0);
    }

    @Override public String getPostfix() { return POSTFIX; }

    @Override
    public @NotNull SoundType getSoundType(final BlockState state,
                                           final @NotNull LevelReader world,
                                           final @NotNull BlockPos pos,
                                           @Nullable final Entity entity) {
        if (!(entity instanceof Player player)) return SoundType.WOOL;

        var held = player.getMainHandItem();
        if (held.isEmpty()) return SoundType.WOOL;

        var paintbrush = BuiltInRegistries.ITEM.get(Wallpapercraft.getId("paintbrush"));
        if (held.getItem() == paintbrush || held.getItem() instanceof PressColour || held.getItem() instanceof PressVariant) {
            return ModSoundTypes.BLOCK_CHANGE;
        }
        return SoundType.WOOL;
    }

    @Override
    public @NotNull BlockState updateShape(final BlockState state,
                                           final @NotNull Direction direction,
                                           final @NotNull BlockState neighborState,
                                           final @NotNull LevelAccessor level,
                                           final @NotNull BlockPos pos,
                                           final @NotNull BlockPos neighborPos) {
        return !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(final @NotNull BlockState state, final LevelReader level, final BlockPos pos) {
        return !level.isEmptyBlock(pos.below());
    }

    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state,
                                        final @NotNull BlockGetter level,
                                        final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext ctx) {
        return SHAPE;
    }
}
