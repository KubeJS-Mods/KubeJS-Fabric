package dev.latvian.kubejs.item;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundItemStackJS extends ItemStackJS {
	private final String item;
	private final Identifier itemRL;
	private int count;
	private MapJS nbt;
	private ItemStack cached;
	
	public UnboundItemStackJS(Identifier i) {
		item = i.toString();
		itemRL = i;
		count = 1;
		nbt = null;
		cached = null;
	}
	
	@Override
	public Item getItem() {
		Item i = Registry.ITEM.get(new Identifier(item));
		
		if (i != null) {
			return i;
		}
		
		return Items.AIR;
	}
	
	@Override
	public ItemStack getItemStack() {
		if (cached == null) {
			Item i = getItem();
			
			if (i == Items.AIR) {
				return ItemStack.EMPTY;
			}
			
			cached = new ItemStack(i, count);
			
			if (nbt != null) {
				cached.setTag(MapJS.nbt(nbt));
			}
		}
		
		return cached;
	}
	
	@Override
	@ID
	public String getId() {
		return item;
	}
	
	@Override
	public boolean isEmpty() {
		return super.isEmpty() || getItem() == Items.AIR;
	}
	
	@Override
	public ItemStackJS getCopy() {
		UnboundItemStackJS stack = new UnboundItemStackJS(itemRL);
		stack.count = count;
		stack.nbt = nbt == null ? null : nbt.copy();
		stack.setChance(getChance());
		return stack;
	}
	
	@Override
	public void setCount(int c) {
		count = MathHelper.clamp(c, 0, 64);
		cached = null;
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public MapJS getNbt() {
		if (nbt == null) {
			nbt = new MapJS();
			nbt.changeListener = this;
		}
		
		return nbt;
	}
	
	@Override
	public boolean areItemsEqual(ItemStackJS stack) {
		if (stack instanceof UnboundItemStackJS) {
			return itemRL.equals(((UnboundItemStackJS) stack).itemRL);
		}
		
		return getItem() == stack.getItem();
	}
	
	@Override
	public boolean areItemsEqual(ItemStack stack) {
		return itemRL.equals(Registry.ITEM.getId(stack.getItem()));
	}
	
	@Override
	public void onChanged(@Nullable MapJS o) {
		cached = null;
	}
}
