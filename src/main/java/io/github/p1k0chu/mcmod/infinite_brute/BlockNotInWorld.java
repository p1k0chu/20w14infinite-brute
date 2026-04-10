package io.github.p1k0chu.mcmod.infinite_brute;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.Nullable;

public class BlockNotInWorld extends BlockInWorld {
    private final BlockState state;

    public BlockNotInWorld(BlockState state) {
        super(null, new BlockPos(0, 0, 0), false);

        this.state = state;
    }

    @Override
    public BlockState getState() {
        return state;
    }

    @Override
    public @Nullable BlockEntity getEntity() {
        return null;
    }

    @Override
    public LevelReader getLevel() {
        throw new AssertionError("no level");
    }

    @Override
    public BlockPos getPos() {
        throw new AssertionError("no block pos");
    }
}
