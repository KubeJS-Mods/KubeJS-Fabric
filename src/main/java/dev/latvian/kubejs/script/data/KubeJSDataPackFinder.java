package dev.latvian.kubejs.script.data;

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
public class KubeJSDataPackFinder implements ResourcePackProvider {
	private final File folder;
	
	public KubeJSDataPackFinder(File f) {
		folder = f;
	}
	
	@Override
	public <T extends ResourcePackProfile> void register(Consumer<T> nameToPackMap, ResourcePackProfile.Factory<T> packInfoFactory) {
		File dataFolder = new File(folder, "data");
		
		if (!dataFolder.exists()) {
			File scriptsFolder = new File(new File(dataFolder, "modpack"), "kubejs");
			scriptsFolder.mkdirs();
			
			try {
				try (PrintWriter exampleJsWriter = new PrintWriter(new FileWriter(new File(scriptsFolder, "example.js")))) {
					exampleJsWriter.println("console.info('Hello, World! (You will see this line every time you start server or run /reload)')");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		KubeJSResourcePack dataPack = new KubeJSResourcePack(folder, ResourceType.SERVER_DATA);
		PackResourceMetadata dataPackMetadata = new PackResourceMetadata(new LiteralText("./kubejs/data/"), 5);
		nameToPackMap.accept((T) new ResourcePackProfile("kubejs:data_pack", true, () -> dataPack, dataPack, dataPackMetadata, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN));
	}
}