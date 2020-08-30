package dev.latvian.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ingredient.IgnoreNBTIngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author LatvianModder
 */
public abstract class ItemStackJS implements IngredientJS, NBTSerializable, WrappedJSObjectChangeListener<MapJS> {
	private static List<ItemStackJS> cachedItemList;
	
	public static ItemStackJS of(@Nullable Object o) {
		if (o == null) {
			return EmptyItemStackJS.INSTANCE;
		} else if (o instanceof ItemStackJS) {
			return (ItemStackJS) o;
		} else if (o instanceof IngredientJS) {
			return ((IngredientJS) o).getFirst();
		} else if (o instanceof ItemStack) {
			ItemStack stack = (ItemStack) o;
			return stack.isEmpty() ? EmptyItemStackJS.INSTANCE : new BoundItemStackJS(stack);
		} else if (o instanceof ResourceLocation) {
			return new UnboundItemStackJS((ResourceLocation) o);
		} else if (o instanceof Item) {
			return new UnboundItemStackJS(Registry.ITEM.getKey(((Item) o)));
		} else if (o instanceof CharSequence) {
			String s = o.toString();
			
			if (s.startsWith("#")) {
				return new TagIngredientJS(new ResourceLocation(s.substring(1)), 1).getFirst();
			}
			
			return new UnboundItemStackJS(new ResourceLocation(s));
		}
		
		MapJS map = MapJS.of(o);
		
		if (map != null) {
			if (map.containsKey("item")) {
				ItemStackJS stack = new UnboundItemStackJS(new ResourceLocation(KubeJS.appendModId(map.get("item").toString())));
				
				if (map.get("count") instanceof Number) {
					stack.setCount(((Number) map.get("count")).intValue());
				}
				
				if (map.containsKey("nbt")) {
					stack.nbt(map.get("nbt"));
				}
				
				return stack;
			} else if (map.get("tag") instanceof CharSequence) {
				int count = 1;
				if (map.containsKey("count")) {
					count = UtilsJS.parseInt(map.get("count"), 1);
				}
				ItemStackJS stack = new TagIngredientJS(new ResourceLocation(map.get("tag").toString()), count).getFirst();
				
				if (map.containsKey("count")) {
					stack.setCount(UtilsJS.parseInt(map.get("count"), 1));
				}
				
				return stack;
			}
		}
		
		String s = String.valueOf(o).trim();
		
		if (s.isEmpty() || s.equals("air")) {
			return EmptyItemStackJS.INSTANCE;
		}
		
		if (s.startsWith("#")) {
			return new TagIngredientJS(new ResourceLocation(s.substring(1)), 1).getFirst();
		}
		
		return new UnboundItemStackJS(new ResourceLocation(s));
	}
	
	public static ItemStackJS of(@Nullable Object o, @Nullable Object countOrNBT) {
		ItemStackJS stack = of(o);
		Object n = UtilsJS.wrap(countOrNBT, JSObjectType.ANY);
		
		if (n instanceof Number) {
			stack.setCount(((Number) n).intValue());
		} else if (n instanceof MapJS) {
			stack.nbt(n);
		}
		
		return stack;
	}
	
	public static ItemStackJS of(@Nullable Object o, int count, @Nullable Object nbt) {
		ItemStackJS stack = of(o);
		stack.setCount(count);
		stack.nbt(nbt);
		return stack;
	}
	
	public static ItemStackJS resultFromRecipeJson(JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return EmptyItemStackJS.INSTANCE;
		} else if (json.isJsonPrimitive()) {
			return of(json.getAsString());
		} else if (json.isJsonObject()) {
			JsonObject o = json.getAsJsonObject();
			
			if (o.has("item")) {
				ItemStackJS stack = ItemStackJS.of(o.get("item").getAsString());
				
				if (o.has("count")) {
					stack.setCount(o.get("count").getAsInt());
				}
				
				if (o.has("nbt")) {
					JsonElement element = o.get("nbt");
					
					if (element.isJsonObject()) {
						stack.nbt(element);
					} else {
						// TODO redo this
//						try
//						{
//							stack.nbt(StringNbtReader.parse(JsonHelper.getString(element, "nbt")));
//						}
//						catch (CommandSyntaxException ex)
//						{
//							ex.printStackTrace();
//						}
					}
				}
				
				if (o.has("chance")) {
					stack.setChance(o.get("chance").getAsDouble());
				}
				
				return stack;
			}
		}
		
		return EmptyItemStackJS.INSTANCE;
	}
	
	public static List<ItemStackJS> getList() {
		if (cachedItemList != null) {
			return cachedItemList;
		}
		
		LinkedHashSet<ItemStackJS> set = new LinkedHashSet<>();
		NonNullList<ItemStack> stackList = NonNullList.create();
		
		for (Item item : Registry.ITEM) {
			item.fillItemCategory(CreativeModeTab.TAB_SEARCH, stackList);
		}
		
		for (ItemStack stack : stackList) {
			if (!stack.isEmpty()) {
				set.add(new BoundItemStackJS(stack).getCopy().count(1));
			}
		}
		
		cachedItemList = Collections.unmodifiableList(Arrays.asList(set.toArray(new ItemStackJS[0])));
		return cachedItemList;
	}
	
	public static void clearListCache() {
		cachedItemList = null;
	}
	
	public static List<String> getTypeList() {
		List<String> list = new ArrayList<>();
		
		for (ResourceLocation id : Registry.ITEM.keySet()) {
			list.add(id.toString());
		}
		
		return list;
	}
	
	private double chance = 1D;
	
	public abstract Item getItem();
	
	@MinecraftClass
	public abstract ItemStack getItemStack();
	
	@ID
	public String getId() {
		return Registry.ITEM.getKey(getItem()).toString();
	}
	
	public abstract ItemStackJS getCopy();
	
	public abstract void setCount(int count);
	
	@Override
	public abstract int getCount();
	
	@Override
	public final ItemStackJS count(int c) {
		setCount(c);
		return this;
	}
	
	public final ItemStackJS x(int c) {
		return count(c);
	}
	
	@Override
	public boolean isEmpty() {
		return getCount() <= 0;
	}
	
	public boolean isBlock() {
		return getItem() instanceof BlockItem;
	}
	
	public abstract MapJS getNbt();
	
	public final ItemStackJS nbt(@Nullable Object o) {
		MapJS nbt = MapJS.of(o);
		
		if (nbt != null) {
			getNbt().putAll(nbt);
		}
		
		return this;
	}
	
	public void setChance(double c) {
		chance = Mth.clamp(c, 0D, 1D);
	}
	
	public double getChance() {
		return chance;
	}
	
	public final ItemStackJS chance(double c) {
		setChance(c);
		return this;
	}
	
	public Text getName() {
		return Text.of(getItemStack().getHoverName());
	}
	
	public void setName(@Nullable Object displayName) {
		if (displayName == null || displayName instanceof String && displayName.toString().isEmpty()) {
			return;
		}
		
		Text t = Text.of(displayName);
		MapJS nbt = getNbt();
		nbt.getOrNewMap("display").put("Name", t.toJson());
	}
	
	public final ItemStackJS name(String displayName) {
		setName(displayName);
		return this;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		int count = getCount();
		double chance = getChance();
		MapJS nbt = getNbt();
		
		if (count > 1 || chance < 1D || !nbt.isEmpty()) {
			builder.append("item.of('");
			builder.append(getId());
			builder.append('\'');
			
			if (count > 1) {
				builder.append(", ");
				builder.append(count);
			}
			
			if (!nbt.isEmpty()) {
				builder.append(", ");
				builder.append(nbt);
			}
			
			builder.append(')');
			
			if (chance < 1D) {
				builder.append(".chance(");
				builder.append(chance);
				builder.append(')');
			}
		} else {
			builder.append('\'');
			builder.append(getId());
			builder.append('\'');
		}
		
		return builder.toString();
	}
	
	@Override
	public boolean test(ItemStackJS stack) {
		return areItemsEqual(stack) && isNBTEqual(stack);
	}
	
	@Override
	public boolean testVanilla(ItemStack stack) {
		return areItemsEqual(stack) && isNBTEqual(stack);
	}
	
	@Override
	public Set<ItemStackJS> getStacks() {
		return Collections.singleton(this);
	}
	
	@Override
	public ItemStackJS getFirst() {
		return getCopy();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getItem(), getNbt());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString()));
		} else if (o instanceof ItemStack) {
			ItemStack s = (ItemStack) o;
			return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
		}
		
		ItemStackJS s = of(o);
		return !s.isEmpty() && areItemsEqual(s) && isNBTEqual(s);
	}
	
	public boolean strongEquals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(UtilsJS.getID(o.toString())) && getCount() == 1 && getNbt().isEmpty();
		} else if (o instanceof ItemStack) {
			ItemStack s = (ItemStack) o;
			return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
		}
		
		ItemStackJS s = of(o);
		return getCount() == s.getCount() && areItemsEqual(s) && isNBTEqual(s);
	}
	
	public MapJS getEnchantments() {
		final MapJS nbt = getNbt();
		final String key = getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
		final MapJS enchantments = new MapJS();
		
		enchantments.changeListener = o -> {
			ListJS list = new ListJS(o.size());
			
			for (Map.Entry<String, Object> entry : o.entrySet()) {
				if (entry.getValue() instanceof Number && ((Number) entry.getValue()).intValue() > 0) {
					MapJS ench = new MapJS(2);
					ench.put("id", new ResourceLocation(entry.getKey()).toString());
					ench.put("lvl", entry.getValue());
					list.add(ench);
				}
			}
			
			if (list.isEmpty()) {
				nbt.remove(key);
			} else {
				nbt.put(key, list);
			}
		};
		
		ListJS list = ListJS.of(nbt.get(key));
		
		if (list != null) {
			for (Object o : list) {
				MapJS m = MapJS.of(o);
				
				if (m != null && m.containsKey("id") && m.containsKey("lvl")) {
					enchantments.put(m.get("id").toString(), m.get("lvl"));
				}
			}
		}
		
		return enchantments;
	}
	
	public ItemStackJS enchant(Object enchantments) {
		getEnchantments().putAll(MapJS.of(enchantments));
		return this;
	}
	
	public String getMod() {
		return Registry.ITEM.getKey(getItem()).getNamespace();
	}
	
	public ListJS getLore() {
		final MapJS nbt = getNbt();
		final ListJS lore = new ListJS();
		
		lore.changeListener = o -> {
			if (lore.isEmpty()) {
				nbt.remove("Lore");
			} else {
				ListJS lore1 = new ListJS(lore.size());
				
				for (Object o1 : lore) {
					lore1.add(net.minecraft.network.chat.Component.Serializer.toJson(Text.of(o1).component()));
				}
				
				nbt.put("Lore", lore1);
			}
		};
		
		ListJS list = ListJS.of(nbt.get("Lore"));
		
		if (list != null) {
			for (Object o : list) {
				try {
					lore.add(net.minecraft.network.chat.Component.Serializer.fromJsonLenient(o.toString()));
				} catch (JsonParseException var19) {
				}
			}
		}
		
		return lore;
	}
	
	public IgnoreNBTIngredientJS ignoreNBT() {
		return new IgnoreNBTIngredientJS(this);
	}
	
	public boolean areItemsEqual(ItemStackJS stack) {
		return getItem() == stack.getItem();
	}
	
	public boolean areItemsEqual(ItemStack stack) {
		return getItem() == stack.getItem();
	}
	
	public boolean isNBTEqual(ItemStackJS stack) {
		return Objects.equals(getNbt(), stack.getNbt());
	}
	
	public boolean isNBTEqual(ItemStack stack) {
		MapJS nbt = getNbt();
		CompoundTag nbt1 = stack.getTag();
		
		if (nbt1 == null) {
			return nbt.isEmpty();
		}
		
		return Objects.equals(MapJS.nbt(nbt), nbt1);
	}
	
	// TODO Make isItemEffective alternatives as this doesn't exist in Fabric.
//	public int getHarvestLevel(Identifier tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block)
//	{
//		ItemStack stack = getItemStack();
//		return stack.getItem().getHarvestLevel(stack, tool, player == null ? null : player.minecraftPlayer, block == null ? null : block.getBlockState());
//	}
//
//	public int getHarvestLevel(Identifier tool)
//	{
//		return getHarvestLevel(tool, null, null);
//	}
	
	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("item", getId());
		
		if (!getNbt().isEmpty()) {
			json.addProperty("tag", getNbt().toNBT().toString());
		}
		
		if (getCount() != 1) {
			json.addProperty("count", getCount());
		}

		return json;
	}
	
	public JsonElement toResultJson() {
		JsonObject json = new JsonObject();
		json.addProperty("item", getId());
		json.addProperty("count", getCount());
		
		MapJS nbt = getNbt();
		
		if (!nbt.isEmpty()) {
			json.addProperty("nbt", nbt.toNBT().toString());
		}
		
		if (getChance() < 1D) {
			json.addProperty("chance", getChance());
		}
		
		return json;
	}
	
	@Override
	public CompoundTag toNBT() {
		return getItemStack().save(new CompoundTag());
	}
	
	@Override
	public void onChanged(@Nullable MapJS o) {
	}
}