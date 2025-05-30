package fr.hugman.promenade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public abstract class AbstractFacingPlantStemBlock extends AbstractFacingPlantPartBlock implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_25;
    public static final int MAX_AGE = 25;
    private final double growthChance;

    public AbstractFacingPlantStemBlock(
            AbstractBlock.Settings settings,
            VoxelShape[] outlineShapes,
            boolean tickWater,
            double growthChance
    ) {
        super(settings, outlineShapes, tickWater);
        this.growthChance = growthChance;
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
    }

    @Override
    protected abstract MapCodec<? extends AbstractFacingPlantStemBlock> getCodec();

    @Override
    public BlockState getRandomGrowthState(Random random) {
        return this.getDefaultState().with(AGE, Integer.valueOf(random.nextInt(MAX_AGE)));
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance) {
            BlockPos blockPos = pos.offset(state.get(FACING));
            if (this.canGrowAt(world.getBlockState(blockPos))) {
                world.setBlockState(blockPos, this.age(state, world.random));
            }
        }
    }

    protected BlockState age(BlockState state, Random random) {
        return state.cycle(AGE);
    }

    public BlockState withMaxAge(BlockState state) {
        return state.with(AGE, Integer.valueOf(MAX_AGE));
    }

    public boolean hasMaxAge(BlockState state) {
        return state.get(AGE) == MAX_AGE;
    }

    protected BlockState copyState(BlockState from, BlockState to) {
        return to.with(FACING, from.get(FACING));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        var facing = state.get(FACING);
        if (direction == facing.getOpposite() && !state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, this, 1);
        }

        if (direction != facing || !((neighborState.isOf(this) || neighborState.isOf(this.getPlant())) && neighborState.get(FACING) == facing)) {
            if (this.tickWater) {
                tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }

            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        } else {
            return this.copyState(state, this.getPlant().getDefaultState());
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AGE);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return this.canGrowAt(world.getBlockState(pos.offset(state.get(FACING))));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        var facing = state.get(FACING);
        var blockPos = pos.offset(facing);
        var i = Math.min(state.get(AGE) + 1, MAX_AGE);
        var j = this.getGrowthLength(random);

        for (int k = 0; k < j && this.canGrowAt(world.getBlockState(blockPos)); k++) {
            world.setBlockState(blockPos, state.with(AGE, Integer.valueOf(i)).with(FACING, facing));
            blockPos = blockPos.offset(facing);
            i = Math.min(i + 1, MAX_AGE);
        }
    }

    protected abstract int getGrowthLength(Random random);

    protected abstract boolean canGrowAt(BlockState state);

    @Override
    protected AbstractFacingPlantStemBlock getStem() {
        return this;
    }
}
