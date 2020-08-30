package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.server.ServerScriptManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DataPackRegistriesHelper {
	public static List<PackResources> getResourcePackListKJS(List<PackResources> list0) {
		List<PackResources> list = new ArrayList<>();
		list.add(ServerScriptManager.instance.virtualDataPackLast);
		list.addAll(list0);
		list.add(new KubeJSResourcePack(KubeJS.getGameDirectory().resolve("kubejs").toFile(), PackType.SERVER_DATA));
		list.add(ServerScriptManager.instance.virtualDataPackFirst);
		return list;
	}
}
