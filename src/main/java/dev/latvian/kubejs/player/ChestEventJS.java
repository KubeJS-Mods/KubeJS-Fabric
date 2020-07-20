package dev.latvian.kubejs.player;

/**
 * @author LatvianModder
 */
public class ChestEventJS {}
//public class ChestEventJS extends InventoryEventJS
//{
//	private InventoryJS inventory;
//
//	public ChestEventJS(PlayerContainerEvent e)
//	{
//		super(e);
//	}
//
//	@MinecraftClass
//	public IInventory getWrappedInventory()
//	{
//		return ((ChestContainer) getInventoryContainer()).getLowerChestInventory();
//	}
//
//	public InventoryJS getInventory()
//	{
//		if (inventory == null)
//		{
//			inventory = new InventoryJS(getWrappedInventory());
//		}
//
//		return inventory;
//	}
//
//	@Nullable
//	public BlockContainerJS getBlock()
//	{
//		if (getWrappedInventory() instanceof BlockEntity)
//		{
//			return getWorld().getBlock((BlockEntity) getWrappedInventory());
//		}
//
//		return null;
//	}
//}