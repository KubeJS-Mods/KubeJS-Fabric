package dev.latvian.kubejs.item;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * @author LatvianModder
 */
public class InventoryJS {
	@MinecraftClass
	public final Container minecraftInventory;
	
	public InventoryJS(Container h) {
		minecraftInventory = h;
	}
	
	public int getSize() {
		return minecraftInventory.getContainerSize();
	}
	
	public ItemStackJS get(int slot) {
		return ItemStackJS.of(minecraftInventory.getItem(slot));
	}
	
	public void set(int slot, Object item) {
		minecraftInventory.setItem(slot, ItemStackJS.of(item).getItemStack());
	}
	
	// TODO
//	public ItemStackJS insert(int slot, Object item, boolean simulate)
//	{
//		return ItemStackJS.of(minecraftInventory.insertItem(slot, ItemStackJS.of(item).getItemStack(), simulate));
//	}
//
//	public ItemStackJS extract(int slot, int amount, boolean simulate)
//	{
//		return ItemStackJS.of(minecraftInventory.extractItem(slot, amount, simulate));
//	}
	
	public int getSlotLimit(int slot) {
		return minecraftInventory.getMaxStackSize();
	}
	
	public boolean isItemValid(int slot, Object item) {
		return minecraftInventory.canPlaceItem(slot, ItemStackJS.of(item).getItemStack());
	}
	
	public void clear() {
		minecraftInventory.clearContent();
	}
	
	public void clear(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);
		
		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			clear();
		}
		
		for (int i = minecraftInventory.getContainerSize(); i >= 0; i--) {
			if (ingredient.testVanilla(minecraftInventory.getItem(i))) {
				minecraftInventory.removeItem(i, minecraftInventory.getItem(i).getCount());
			}
		}
	}
	
	public int find() {
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			ItemStack stack1 = minecraftInventory.getItem(i);
			
			if (!stack1.isEmpty()) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int find(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);
		
		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return find();
		}
		
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			ItemStack stack1 = minecraftInventory.getItem(i);
			
			if (ingredient.testVanilla(stack1)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int count() {
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			count += minecraftInventory.getItem(i).getCount();
		}
		
		return count;
	}
	
	public int count(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);
		
		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return count();
		}
		
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			ItemStack stack1 = minecraftInventory.getItem(i);
			
			if (ingredient.testVanilla(stack1)) {
				count += stack1.getCount();
			}
		}
		
		return count;
	}
	
	public int countNonEmpty() {
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			if (!minecraftInventory.getItem(i).isEmpty()) {
				count++;
			}
		}
		
		return count;
	}
	
	public int countNonEmpty(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);
		
		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return countNonEmpty();
		}
		
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			ItemStack stack1 = minecraftInventory.getItem(i);
			
			if (ingredient.testVanilla(stack1)) {
				count++;
			}
		}
		
		return count;
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < minecraftInventory.getContainerSize(); i++) {
			if (!minecraftInventory.getItem(i).isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		LinkedList<String> list = new LinkedList<>();
		
		for (int i = 0; i < getSize(); i++) {
			list.add(get(i).toString());
		}
		
		return list.toString();
	}
	
	public void markDirty() {
		minecraftInventory.setChanged();
	}
	
	@Nullable
	public BlockContainerJS getBlock(WorldJS world) {
		if (minecraftInventory instanceof BlockEntity) {
			return world.getBlock((BlockEntity) minecraftInventory);
		}
		
		return null;
	}
}