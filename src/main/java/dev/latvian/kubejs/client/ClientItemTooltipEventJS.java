package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ClientItemTooltipEventJS extends EventJS
{
	private final ItemStack stack;
	private final List<net.minecraft.text.Text> lines;

	public ClientItemTooltipEventJS(ItemStack stack, TooltipContext context, List<net.minecraft.text.Text> lines)
	{
		this.stack = stack;
		this.lines = lines;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(stack);
	}

	public void add(Object text)
	{
		lines.add(Text.of(text).component());
	}
}