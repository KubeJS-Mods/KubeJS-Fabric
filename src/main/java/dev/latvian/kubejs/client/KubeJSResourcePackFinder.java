package dev.latvian.kubejs.client;

import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class KubeJSResourcePackFinder implements ResourcePackProvider {
	private final File folder;
	
	public KubeJSResourcePackFinder(File f) {
		folder = f;
	}
	
	@Override
	public void register(Consumer<ResourcePackProfile> nameToPackMap, ResourcePackProfile.Factory packInfoFactory) {
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
		
		KubeJSResourcePack pack = new KubeJSResourcePack(folder, ResourceType.CLIENT_RESOURCES);
		PackResourceMetadata metadataSection = new PackResourceMetadata(new LiteralText("./kubejs/assets/"), 5);
		nameToPackMap.accept(new ResourcePackProfile("kubejs:resource_pack", true, () -> pack, pack, metadataSection, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN));
	}
}