package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.core.TagBuilderKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends ServerEventJS {
	public static class TagWrapper<T> {
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final Tag.Builder builder;
		private final List<Tag.BuilderEntry> proxyList;
		
		private TagWrapper(TagEventJS<T> e, ResourceLocation i, Tag.Builder t) {
			event = e;
			id = i;
			builder = t;
			proxyList = ((TagBuilderKJS) builder).getProxyListKJS();
		}
		
		public TagWrapper<T> add(Object ids) {
			for (Object o : ListJS.orSelf(ids)) {
				String s = String.valueOf(o);
				
				if (s.startsWith("#")) {
					TagWrapper<T> w = event.get(s.substring(1));
					builder.addTag(w.id, KubeJS.MOD_ID);
					event.addedCount += w.proxyList.size();
					ScriptType.SERVER.console.getLogger().info("+ " + event.type + ":" + id + " // " + w.id);
				} else {
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.registry.apply(sid);
					
					if (v.isPresent()) {
						builder.addElement(sid, KubeJS.MOD_ID);
						event.addedCount++;
						ScriptType.SERVER.console.getLogger().info("+ " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					} else {
						ScriptType.SERVER.console.getLogger().warn("+ " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}
			
			return this;
		}
		
		public TagWrapper<T> remove(Object ids) {
			for (Object o : ListJS.orSelf(ids)) {
				String s = String.valueOf(o);
				
				if (s.startsWith("#")) {
					TagWrapper<T> w = event.get(s.substring(1));
					Tag.TagEntry entry = new Tag.TagEntry(w.id);
					proxyList.removeIf(p -> entry.equals(p.getEntry()));
					event.addedCount += w.proxyList.size();
					ScriptType.SERVER.console.getLogger().info("- " + event.type + ":" + id + " // " + w.id);
				} else {
					ResourceLocation sid = new ResourceLocation(s);
					Optional<T> v = event.registry.apply(sid);
					
					if (v.isPresent()) {
						Tag.ElementEntry entry = new Tag.ElementEntry(sid);
						proxyList.removeIf(p -> entry.equals(p.getEntry()));
						event.addedCount++;
						ScriptType.SERVER.console.getLogger().info("- " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					} else {
						ScriptType.SERVER.console.getLogger().warn("- " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}
			
			return this;
		}
	}
	
	private final String type;
	private final Map<ResourceLocation, Tag.Builder> map;
	private final Function<ResourceLocation, Optional<T>> registry;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;
	
	public TagEventJS(String t, Map<ResourceLocation, Tag.Builder> m, Function<ResourceLocation, Optional<T>> r) {
		type = t;
		map = m;
		registry = r;
	}
	
	public String getType() {
		return type;
	}
	
	public void post(String event) {
		tags = new HashMap<>();
		
		for (Map.Entry<ResourceLocation, Tag.Builder> entry : map.entrySet()) {
			TagWrapper<T> w = new TagWrapper<>(this, entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ScriptType.SERVER.console.getLogger().debug(type + "/#" + entry.getKey() + "; " + w.proxyList.size());
		}
		
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, event);
		post(ScriptType.SERVER, "server.datapack.tags." + type); //TODO: To be removed
		ScriptType.SERVER.console.setLineNumber(false);
		
		ScriptType.SERVER.console.getLogger().info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + ", removed " + removedCount);
	}
	
	public TagWrapper<T> get(@ID String tag) {
		ResourceLocation id = UtilsJS.getMCID(tag);
		TagWrapper<T> t = tags.get(id);
		
		if (t == null) {
			t = new TagWrapper<>(this, id, Tag.Builder.tag());
			tags.put(id, t);
			map.put(id, t.builder);
		}
		
		return t;
	}
}