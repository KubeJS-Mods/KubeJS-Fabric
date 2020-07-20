package dev.latvian.kubejs.text;

import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum TextColor {
	BLACK("black", '0', 0x000000, Formatting.BLACK),
	DARK_BLUE("dark_blue", '1', 0x0000AA, Formatting.DARK_BLUE),
	DARK_GREEN("dark_green", '2', 0x00AA00, Formatting.DARK_GREEN),
	DARK_AQUA("dark_aqua", '3', 0x00AAAA, Formatting.DARK_AQUA),
	DARK_RED("dark_red", '4', 0xAA0000, Formatting.DARK_RED),
	DARK_PURPLE("dark_purple", '5', 0xAA00AA, Formatting.DARK_PURPLE),
	GOLD("gold", '6', 0xFFAA00, Formatting.GOLD),
	GRAY("gray", '7', 0xAAAAAA, Formatting.GRAY),
	DARK_GRAY("dark_gray", '8', 0x555555, Formatting.DARK_GRAY),
	BLUE("blue", '9', 0x5555FF, Formatting.BLUE),
	GREEN("green", 'a', 0x55FF55, Formatting.GREEN),
	AQUA("aqua", 'b', 0x55FFFF, Formatting.AQUA),
	RED("red", 'c', 0xFF5555, Formatting.RED),
	LIGHT_PURPLE("light_purple", 'd', 0xFF55FF, Formatting.LIGHT_PURPLE),
	YELLOW("yellow", 'e', 0xFFFF55, Formatting.YELLOW),
	WHITE("white", 'f', 0xFFFFFF, Formatting.WHITE);
	
	public static final Map<String, TextColor> MAP = new HashMap<>();
	
	static {
		for (TextColor color : values()) {
			MAP.put(color.name, color);
		}
	}
	
	public final String name;
	public final char code;
	public final int color;
	public final Formatting textFormatting;
	
	TextColor(String n, char c, int h, Formatting f) {
		name = n;
		code = c;
		color = 0xFF000000 | h;
		textFormatting = f;
	}
}