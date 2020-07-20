package dev.latvian.kubejs.item;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * @author LatvianModder
 */
public class InventoryJS {
	@MinecraftClass
	public final Inventory minecraftInventory;
	
	public InventoryJS(Inventory h) {
		minecraftInventory = h;
	}
	
	public int getSize() {
		return minecraftInventory.size();
	}
	
	public ItemStackJS get(int slot) {
		return ItemStackJS.of(minecraftInventory.getStack(slot));
	}
	
	public void set(int slot, Object item) {
		minecraftInventory.setStack(slot, ItemStackJS.of(item).getItemStack());
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
		return minecraftInventory.getMaxCountPerStack();
	}
	
	public boolean isItemValid(int slot, Object item) {
		return minecraftInventory.isValid(slot, ItemStackJS.of(item).getItemStack());
	}
	
	public void clear() {
		minecraftInventory.clear();
	}
	
	public void clear(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);
		
		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			clear();
		}
		
		for (int i = minecraftInventory.size(); i >= 0; i--) {
			if (ingredient.testVanilla(minecraftInventory.getStack(i))) {
				minecraftInventory.removeStack(i, minecraftInventory.getStack(i).getCount());
			}
		}
	}
	
	public int find() {
		for (int i = 0; i < minecraftInventory.size(); i++) {
			ItemStack stack1 = minecraftInventory.getStack(i);
			
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
		
		for (int i = 0; i < minecraftInventory.size(); i++) {
			ItemStack stack1 = minecraftInventory.getStack(i);
			
			if (ingredient.testVanilla(stack1)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int count() {
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.size(); i++) {
			count += minecraftInventory.getStack(i).getCount();
		}
		
		return count;
	}
	
	public int count(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);
		
		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return count();
		}
		
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.size(); i++) {
			ItemStack stack1 = minecraftInventory.getStack(i);
			
			if (ingredient.testVanilla(stack1)) {
				count += stack1.getCount();
			}
		}
		
		return count;
	}
	
	public int countNonEmpty() {
		int count = 0;
		
		for (int i = 0; i < minecraftInventory.size(); i++) {
			if (!minecraftInventory.getStack(i).isEmpty()) {
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
		
		for (int i = 0; i < minecraftInventory.size(); i++) {
			ItemStack stack1 = minecraftInventory.getStack(i);
			
			if (ingredient.testVanilla(stack1)) {
				count++;
			}
		}
		
		return count;
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < minecraftInventory.size(); i++) {
			if (!minecraftInventory.getStack(i).isEmpty()) {
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
		minecraftInventory.markDirty();
	}
	
	@Nullable
	public BlockContainerJS getBlock(WorldJS world) {
		if (minecraftInventory instanceof BlockEntity) {
			return world.getBlock((BlockEntity) minecraftInventory);
		}
		
		return null;
	}
}