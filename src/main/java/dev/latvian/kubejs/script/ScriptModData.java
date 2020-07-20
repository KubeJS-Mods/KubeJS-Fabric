package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class ScriptModData {
	private static ScriptModData instance;
	
	public static ScriptModData getInstance() {
		if (instance == null) {
			instance = new ScriptModData("forge", "1.14.4", FabricLoader.getInstance().getAllMods());
		}
		
		return instance;
	}
	
	public static class ModInfo {
		private final String id;
		private String name;
		private String version;
		
		public ModInfo(String i) {
			id = i;
			name = id;
			version = "0.0.0";
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public String getVersion() {
			return version;
		}
	}
	
	private final String type;
	private final String mcVersion;
	private final HashSet<String> list;
	
	public ScriptModData(String t, String mc, Collection<ModContainer> modList) {
		type = t;
		mcVersion = mc;
		list = new HashSet<>(modList.size());
		
		for (ModContainer info : modList) {
			list.add(info.getMetadata().getId());
		}
	}
	
	public String getType() {
		return type;
	}
	
	public String getMcVersion() {
		return mcVersion;
	}
	
	public Set<String> getList() {
		return list;
	}
	
	public String getModVersion() {
		return FabricLoader.getInstance().getModContainer(KubeJS.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
	}
	
	public boolean isLoaded(String modId) {
		return list.contains(modId);
	}
	
	public ModInfo getInfo(String modID) {
		ModInfo info = new ModInfo(modID);
		
		Optional<? extends ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modID);
		
		if (modContainer.isPresent()) {
			ModMetadata i = modContainer.get().getMetadata();
			info.name = i.getName();
			info.version = i.getVersion().toString();
		}
		
		return info;
	}
}