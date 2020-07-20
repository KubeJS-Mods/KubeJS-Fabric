package dev.latvian.kubejs.script.data;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class VirtualKubeJSDataPack extends AbstractFileResourcePack
{
	public final boolean first;
	private final Map<Identifier, String> locationToData;
	private final Map<String, String> pathToData;
	private final Set<String> namespaces;

	public VirtualKubeJSDataPack(boolean f)
	{
		super(new File("dummy"));
		first = f;
		locationToData = new HashMap<>();
		pathToData = new HashMap<>();
		namespaces = new HashSet<>();
	}

	public void addData(Identifier id, String data)
	{
		locationToData.put(id, data);
		pathToData.put("data/" + id.getNamespace() + "/" + id.getPath(), data);
		namespaces.add(id.getNamespace());
	}

	public void resetData()
	{
		locationToData.clear();
		pathToData.clear();
		namespaces.clear();
	}

	@Override
	public InputStream openFile(String path) throws IOException
	{
		String s = pathToData.get(path);

		if (s != null)
		{
			if (ServerSettings.instance.dataPackOutput)
			{
				ScriptType.SERVER.console.info("Served virtual file '" + path + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(path);
	}

	@Override
	public InputStream open(ResourceType type, Identifier location) throws IOException
	{
		String s = locationToData.get(location);

		if (s != null)
		{
			if (ServerSettings.instance.dataPackOutput)
			{
				ScriptType.SERVER.console.info("Served virtual file '" + location + "': " + s);
			}

			return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		}

		throw new FileNotFoundException(location.toString());
	}

	@Override
	public boolean containsFile(String path)
	{
		return pathToData.containsKey(path);
	}

	@Override
	public boolean contains(ResourceType type, Identifier location)
	{
		return type == ResourceType.SERVER_DATA && locationToData.containsKey(location);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String path, int maxDepth, Predicate<String> filter)
	{
		List<Identifier> list = Lists.newArrayList();

		for (Identifier key : locationToData.keySet())
		{
			if (namespace.equals(key.getNamespace()))
			{
				try
				{
					int i = key.getPath().lastIndexOf('/');
					String p = i == -1 ? key.getPath() : key.getPath().substring(i + 1);

					if (key.getPath().startsWith(path) && filter.test(p))
					{
						list.add(key);
					}
				}
				catch (Exception ex)
				{
				}
			}
		}

		return list;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type)
	{
		return new HashSet<>(namespaces);
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> serializer)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "KubeJS Virtual Data Pack [First: " + first + "]";
	}

	@Override
	public void close()
	{
	}

	public boolean hasNamespace(String key)
	{
		return namespaces.contains(key);
	}
}
