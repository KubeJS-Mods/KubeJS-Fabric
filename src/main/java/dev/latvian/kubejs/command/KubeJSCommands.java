package dev.latvian.kubejs.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSCommands
{
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(CommandManager.literal("kubejs")
				.then(CommandManager.literal("item_info")
						.executes(context -> itemInfo(context.getSource().getPlayer()))
				)
				.then(CommandManager.literal("hand")
						.executes(context -> hand(context.getSource().getPlayer()))
				)
				.then(CommandManager.literal("output_recipes")
						.executes(context -> outputRecipes(context.getSource().getPlayer()))
				)
				.then(CommandManager.literal("input_recipes")
						.executes(context -> inputRecipes(context.getSource().getPlayer()))
				)
				.then(CommandManager.literal("check_recipe_conflicts")
						.executes(context -> checkRecipeConflicts(context.getSource().getPlayer()))
				)
		);
	}

	private static int itemInfo(ServerPlayerEntity player)
	{
		ItemStack stack = player.getMainHandStack();
		MutableText c = stack.getName().shallowCopy();
		c.styled(style -> style.withBold(true));
		player.sendSystemMessage(new LiteralText("=== ").formatted(Formatting.GREEN).append(c).append(" ==="), Util.NIL_UUID);

		player.sendSystemMessage(new LiteralText("= Item Tags =").formatted(Formatting.YELLOW), Util.NIL_UUID);

		List<Identifier> tags = new ArrayList<>(getTagsFor(player.world.getTagManager().items(), stack.getItem()));
		tags.sort(null);

		for (Identifier id : tags)
		{
			MutableText component = new LiteralText("- " + id);
			c.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id.toString())));
			c.styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy"))));
			player.sendSystemMessage(component, Util.NIL_UUID);
		}

		return Command.SINGLE_SUCCESS;
	}

	public static <T> Collection<Identifier> getTagsFor(TagContainer<T> container, T object)
	{
		List<Identifier> list = Lists.newArrayList();

		for (Map.Entry<Identifier, Tag<T>> identifierTagEntry : container.getEntries().entrySet())
		{
			Map.Entry<Identifier, Tag<T>> entry = identifierTagEntry;
			if (entry.getValue().contains(object))
			{
				list.add(entry.getKey());
			}
		}

		return list;
	}

	private static int hand(ServerPlayerEntity player)
	{
		ItemStackJS is = ItemStackJS.of(player.getMainHandStack());
		MutableText component = new LiteralText(is.toString() + " [Click to copy]");
		component.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, is.toString())));
		player.sendSystemMessage(component, Util.NIL_UUID);
		return 1;
	}

	private static int outputRecipes(ServerPlayerEntity player)
	{
		player.sendSystemMessage(new LiteralText("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int inputRecipes(ServerPlayerEntity player)
	{
		player.sendSystemMessage(new LiteralText("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int checkRecipeConflicts(ServerPlayerEntity player)
	{
		player.sendSystemMessage(new LiteralText("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
}