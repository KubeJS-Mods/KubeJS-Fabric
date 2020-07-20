package dev.latvian.kubejs.block;

/**
 * @author LatvianModder
 */
public class BlockPlaceEventJS {}
//public class BlockPlaceEventJS extends EntityEventJS
//{
//	public final BlockEvent.EntityPlaceEvent event;
//
//	public BlockPlaceEventJS(BlockEvent.EntityPlaceEvent e)
//	{
//		event = e;
//	}
//
//	@Override
//	public boolean canCancel()
//	{
//		return true;
//	}
//
//	@Override
//	public EntityJS getEntity()
//	{
//		return entityOf(event.getEntity());
//	}
//
//	public BlockContainerJS getBlock()
//	{
//		return new BlockContainerJS(event.getWorld(), event.getPos())
//		{
//			@Override
//			public BlockState getBlockState()
//			{
//				return event.getState();
//			}
//		};
//	}
//}