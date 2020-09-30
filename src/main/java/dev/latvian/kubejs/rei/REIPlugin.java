package dev.latvian.kubejs.rei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class REIPlugin implements REIPluginV0 {
	@Override
	public ResourceLocation getPluginIdentifier() {
		return new ResourceLocation(KubeJS.MOD_ID, "rei");
	}
	
	@Override
	public void registerEntries(EntryRegistry entryRegistry) {
		new HideREIEventJS<>(entryRegistry, EntryStack.Type.ITEM, object -> {
			List<EntryStack> list = new ArrayList<>();
			
			for (ItemStackJS stack : IngredientJS.of(object).getStacks()) {
				list.add(EntryStack.create(stack.getItemStack()));
			}
			
			return list;
		}).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_ITEMS);
		
		new AddREIEventJS(entryRegistry, o -> EntryStack.create(ItemStackJS.of(o).getItemStack())).post(ScriptType.CLIENT, REIIntegration.REI_ADD_ITEMS);
	}
	
	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		new InformationREIEventJS().post(ScriptType.CLIENT, REIIntegration.REI_INFORMATION);
	}
}
