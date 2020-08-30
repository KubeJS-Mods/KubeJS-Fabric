package dev.latvian.kubejs.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		dispatcher.register(Commands.literal("kubejs")
				.then(Commands.literal("item_info")
						.executes(context -> itemInfo(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("hand")
						.executes(context -> hand(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("output_recipes")
						.executes(context -> outputRecipes(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("input_recipes")
						.executes(context -> inputRecipes(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("check_recipe_conflicts")
						.executes(context -> checkRecipeConflicts(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("wiki")
						.executes(context -> wiki(context.getSource()))
				)
		);
	}
	
	private static int itemInfo(ServerPlayer player) {
		ItemStack stack = player.getMainHandItem();
		MutableComponent c = stack.getHoverName().copy();
		c.withStyle(style -> style.withBold(true));
		player.sendMessage(new TextComponent("=== ").withStyle(ChatFormatting.GREEN).append(c).append(" ==="), Util.NIL_UUID);
		
		player.sendMessage(new TextComponent("= Item Tags =").withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
		
		List<ResourceLocation> tags = new ArrayList<>(getTagsFor(player.level.getTagManager().getItems(), stack.getItem()));
		tags.sort(null);
		
		for (ResourceLocation id : tags) {
			MutableComponent component = new TextComponent("- " + id);
			c.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id.toString())));
			c.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy"))));
			player.sendMessage(component, Util.NIL_UUID);
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	public static <T> Collection<ResourceLocation> getTagsFor(TagCollection<T> container, T object) {
		List<ResourceLocation> list = Lists.newArrayList();
		
		for (Map.Entry<ResourceLocation, Tag<T>> identifierTagEntry : container.getAllTags().entrySet()) {
			Map.Entry<ResourceLocation, Tag<T>> entry = identifierTagEntry;
			if (entry.getValue().contains(object)) {
				list.add(entry.getKey());
			}
		}
		
		return list;
	}
	
	private static int hand(ServerPlayer player) {
		ItemStackJS is = ItemStackJS.of(player.getMainHandItem());
		MutableComponent component = new TextComponent(is.toString() + " [Click to copy]");
		component.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, is.toString())));
		player.sendMessage(component, Util.NIL_UUID);
		return 1;
	}
	
	private static int outputRecipes(ServerPlayer player) {
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int inputRecipes(ServerPlayer player) {
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int checkRecipeConflicts(ServerPlayer player) {
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int wiki(CommandSourceStack source) {
		source.sendSuccess(new TextComponent("Click here to open the Wiki").withStyle(ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mods.latvian.dev/books/kubejs"))), false);
		return Command.SINGLE_SUCCESS;
	}
}