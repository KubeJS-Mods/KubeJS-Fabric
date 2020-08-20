package dev.latvian.kubejs.script;

import net.minecraft.util.Lazy;

import javax.annotation.Nullable;
import javax.script.Bindings;
import java.io.Reader;

/**
 * @author LatvianModder
 */
public class ScriptFile implements Comparable<ScriptFile> {
	public final ScriptPack pack;
	public final ScriptFileInfo info;
	public final ScriptSource source;
	public final Lazy<String> babel = new Lazy<>(this::loadBabel);
	
	private Throwable error;
	
	public ScriptFile(ScriptPack p, ScriptFileInfo i, ScriptSource s) {
		pack = p;
		info = i;
		source = s;
	}
	
	@Nullable
	public Throwable getError() {
		return error;
	}
	
	private String loadBabel() {
		error = null;
		
		try (Reader reader = source.createReader(info)) {
			return BabelExecutor.process(reader);
		} catch (Throwable ex) {
			error = ex;
			return null;
		}
	}
	
	public boolean load(Bindings bindings) {
		error = null;
		
		String s = babel.get();
		if (s != null) {
			try {
				pack.engine.eval(s, bindings);
				return true;
			} catch (Throwable ex) {
				error = ex;
			}
		}
		return false;
	}
	
	@Override
	public int compareTo(ScriptFile o) {
		return Integer.compare(o.info.getPriority(), info.getPriority());
	}
}