package dev.latvian.kubejs.client;

import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class KubeJSResourcePackFinder implements RepositorySource {
	private final File folder;
	
	public KubeJSResourcePackFinder(File f) {
		folder = f;
	}
	
	@Override
	public void loadPacks(Consumer<Pack> nameToPackMap, Pack.PackConstructor packInfoFactory) {
		File assetsFolder = new File(folder, "assets");
		
		if (!assetsFolder.exists()) {
			assetsFolder.mkdirs();
			
			File langFolder = new File(new File(assetsFolder, "modpack"), "lang");
			langFolder.mkdirs();
			
			try {
				try (PrintWriter initWriter = new PrintWriter(new FileWriter(new File(langFolder, "en_us.json")))) {
					initWriter.println("{");
					initWriter.println("\t\"modpack.example.translation_key\": \"Example Translation\"");
					initWriter.println("}");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		KubeJSResourcePack pack = new KubeJSResourcePack(folder, PackType.CLIENT_RESOURCES);
		PackMetadataSection metadataSection = new PackMetadataSection(new TextComponent("./kubejs/assets/"), 5);
		nameToPackMap.accept(new Pack("kubejs:resource_pack", true, () -> pack, pack, metadataSection, Pack.Position.TOP, PackSource.BUILT_IN));
	}
}