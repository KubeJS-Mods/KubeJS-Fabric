package dev.latvian.kubejs.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public final BlockBuilder properties;
	public VoxelShape shape;

	public BlockJS(BlockBuilder p)
	{
		super(p.createProperties());
		properties = p;
		shape = VoxelShapes.fullCube();

		if (!properties.customShape.isEmpty())
		{
			List<VoxelShape> s = new ArrayList<>(properties.customShape);
			shape = s.get(0);

			if (s.size() > 1)
			{
				s.remove(0);
				shape = VoxelShapes.union(shape, s.toArray(new VoxelShape[0]));
			}
		}

		if (properties.waterlogged)
		{
			setDefaultState(stateManager.getDefaultState().with(Properties.WATERLOGGED, false));
		}
	}

	@Override
	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return shape;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		if (BlockBuilder.current.waterlogged)
		{
			builder.add(Properties.WATERLOGGED);
		}
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state)
	{
		return properties.waterlogged && state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context)
	{
		if (!properties.waterlogged)
		{
			return getDefaultState();
		}

		return getDefaultState().with(Properties.WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos pos, BlockPos facingPos)
	{
		if (properties.waterlogged && state.get(Properties.WATERLOGGED))
		{
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return state;
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView reader, BlockPos pos)
	{
		return !(properties.waterlogged && state.get(Properties.WATERLOGGED));
	}
}