package dev.latvian.kubejs.block;

/**
 * @author LatvianModder
 */
public class BlockBreakEventJS {}
//public class BlockBreakEventJS extends PlayerEventJS
//{
//	public final BlockEvent.BreakEvent event;
//
//	public BlockBreakEventJS(BlockEvent.BreakEvent e)
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
//		return entityOf(event.getPlayer());
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
//
//	public int getXp()
//	{
//		return event.getExpToDrop();
//	}
//
//	public void setXp(int xp)
//	{
//		event.setExpToDrop(xp);
//	}
//}