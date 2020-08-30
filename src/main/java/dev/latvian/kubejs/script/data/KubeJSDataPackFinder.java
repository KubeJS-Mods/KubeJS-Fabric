package dev.latvian.kubejs.script.data;

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
public class KubeJSDataPackFinder implements RepositorySource {
	private final File folder;
	
	public KubeJSDataPackFinder(File f) {
		folder = f;
	}
	
	@Override
	public void loadPacks(Consumer<Pack> nameToPackMap, Pack.PackConstructor packInfoFactory) {
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
		
		KubeJSResourcePack dataPack = new KubeJSResourcePack(folder, PackType.SERVER_DATA);
		PackMetadataSection dataPackMetadata = new PackMetadataSection(new TextComponent("./kubejs/data/"), 5);
		nameToPackMap.accept(new Pack("kubejs:data_pack", true, () -> dataPack, dataPack, dataPackMetadata, Pack.Position.TOP, PackSource.BUILT_IN));
	}
}