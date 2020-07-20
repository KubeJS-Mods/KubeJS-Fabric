package dev.latvian.kubejs.script;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptPackInfo {
	public final String namespace;
	public final Text displayName;
	public final List<ScriptFileInfo> scripts;
	public final String pathStart;
	
	public ScriptPackInfo(String n, String p) {
		namespace = n;
		scripts = new ArrayList<>();
		pathStart = p;
		displayName = new LiteralText(namespace); // Load custom properties
	}
}