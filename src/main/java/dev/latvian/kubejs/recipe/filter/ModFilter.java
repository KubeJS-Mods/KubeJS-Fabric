package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class ModFilter implements RecipeFilter {
	private final String mod;
	
	public ModFilter(String m) {
		mod = m;
	}
	
	@Override
	public boolean test(RecipeJS r) {
		return r.getMod().equals(mod);
	}
}