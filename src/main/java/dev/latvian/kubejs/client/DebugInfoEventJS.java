package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class DebugInfoEventJS extends EventJS
		// TODO
{
//	public final transient RenderGameOverlayEvent.Text event;
//
//	public DebugInfoEventJS(RenderGameOverlayEvent.Text e)
//	{
//		event = e;
//	}

	public boolean getShowDebug()
	{
		return MinecraftClient.getInstance().options.debugEnabled;
	}

//	public List<String> getLeft()
//	{
//		return event.getLeft();
//	}
//
//	public List<String> getRight()
//	{
//		return event.getRight();
//	}
}