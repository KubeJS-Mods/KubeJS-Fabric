package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.core.TagBuilderKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class TagEventJS<T> extends ServerEventJS
{
	public static class TagWrapper<T>
	{
		private final TagEventJS<T> event;
		private final Identifier id;
		private final Tag.Builder builder;
		private final List<Tag.TrackedEntry> proxyList;

		private TagWrapper(TagEventJS<T> e, Identifier i, Tag.Builder t)
		{
			event = e;
			id = i;
			builder = t;
			proxyList = ((TagBuilderKJS) builder).getProxyListKJS();
		}

		public TagWrapper<T> add(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					TagWrapper<T> w = event.get(s.substring(1));
					builder.addTag(w.id, KubeJS.MOD_ID);
					event.addedCount += w.proxyList.size();
					ScriptType.SERVER.console.logger.info("+ " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					Identifier sid = new Identifier(s);
					Optional<T> v = event.registry.getOrEmpty(sid);

					if (v.isPresent())
					{
						builder.add(sid, KubeJS.MOD_ID);
						event.addedCount++;
						ScriptType.SERVER.console.logger.info("+ " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.logger.warn("+ " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}

			return this;
		}

		public TagWrapper<T> remove(Object ids)
		{
			for (Object o : ListJS.orSelf(ids))
			{
				String s = String.valueOf(o);

				if (s.startsWith("#"))
				{
					TagWrapper<T> w = event.get(s.substring(1));
					Tag.TagEntry entry = new Tag.TagEntry(w.id);
					proxyList.removeIf(p -> entry.equals(p.getEntry()));
					event.addedCount += w.proxyList.size();
					ScriptType.SERVER.console.logger.info("- " + event.type + ":" + id + " // " + w.id);
				}
				else
				{
					Identifier sid = new Identifier(s);
					Optional<T> v = event.registry.getOrEmpty(sid);

					if (v.isPresent())
					{
						Tag.ObjectEntry entry = new Tag.ObjectEntry(sid);
						proxyList.removeIf(p -> entry.equals(p.getEntry()));
						event.addedCount++;
						ScriptType.SERVER.console.logger.info("- " + event.type + ":" + id + " // " + s + " [" + v.get().getClass().getName() + "]");
					}
					else
					{
						ScriptType.SERVER.console.logger.warn("- " + event.type + ":" + id + " // " + s + " [Not found!]");
					}
				}
			}

			return this;
		}
	}

	private final String type;
	private final Map<Identifier, Tag.Builder> map;
	private final Registry<T> registry;
	private Map<Identifier, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;

	public TagEventJS(String t, Map<Identifier, Tag.Builder> m, Registry<T> r)
	{
		type = t;
		map = m;
		registry = r;
	}

	public String getType()
	{
		return type;
	}

	public void post(String event)
	{
		tags = new HashMap<>();

		for (Map.Entry<Identifier, Tag.Builder> entry : map.entrySet())
		{
			TagWrapper<T> w = new TagWrapper<>(this, entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ScriptType.SERVER.console.logger.debug(type + "/#" + entry.getKey() + "; " + w.proxyList.size());
		}

		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, event);
		post(ScriptType.SERVER, "server.datapack.tags." + type); //TODO: To be removed
		ScriptType.SERVER.console.setLineNumber(false);

		ScriptType.SERVER.console.logger.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + ", removed " + removedCount);
	}

	public TagWrapper<T> get(@ID String tag)
	{
		Identifier id = UtilsJS.getMCID(tag);
		TagWrapper<T> t = tags.get(id);

		if (t == null)
		{
			t = new TagWrapper<>(this, id, Tag.Builder.create());
			tags.put(id, t);
			map.put(id, t.builder);
		}

		return t;
	}
}