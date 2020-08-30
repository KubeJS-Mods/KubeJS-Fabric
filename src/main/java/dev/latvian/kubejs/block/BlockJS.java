package dev.latvian.kubejs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block {
	public final BlockBuilder properties;
	public VoxelShape shape;
	
	public BlockJS(BlockBuilder p) {
		super(p.createProperties());
		properties = p;
		shape = Shapes.block();
		
		if (!properties.customShape.isEmpty()) {
			List<VoxelShape> s = new ArrayList<>(properties.customShape);
			shape = s.get(0);
			
			if (s.size() > 1) {
				s.remove(0);
				shape = Shapes.or(shape, s.toArray(new VoxelShape[0]));
			}
		}
		
		if (properties.waterlogged) {
			registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
		}
	}
	
	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return shape;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		if (BlockBuilder.current.waterlogged) {
			builder.add(BlockStateProperties.WATERLOGGED);
		}
	}
	
	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return properties.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (!properties.waterlogged) {
			return defaultBlockState();
		}
		
		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}
	
	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
		if (properties.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		
		return state;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !(properties.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED));
	}
}