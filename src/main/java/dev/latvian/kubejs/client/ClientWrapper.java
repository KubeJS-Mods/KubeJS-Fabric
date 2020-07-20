package dev.latvian.kubejs.client;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class ClientWrapper
{
	@MinecraftClass
	public MinecraftClient getMinecraft()
	{
		return MinecraftClient.getInstance();
	}

	@Nullable
	public ClientWorldJS getWorld()
	{
		return ClientWorldJS.instance;
	}

	@Nullable
	public ClientPlayerJS getPlayer()
	{
		if (ClientWorldJS.instance == null)
		{
			return null;
		}

		return ClientWorldJS.instance.clientPlayerData.getPlayer();
	}

	@Nullable
	public Screen getCurrentGui()
	{
		return getMinecraft().currentScreen;
	}

	public void setCurrentGui(Screen gui)
	{
		getMinecraft().openScreen(gui);
	}

	public void setTitle(String t)
	{
		ClientProperties.get().title = t.trim();
		getMinecraft().updateWindowTitle();
	}

	public String getCurrentWorldName()
	{
		if (getMinecraft().getCurrentServerEntry() != null)
		{
			return getMinecraft().getCurrentServerEntry().name;
		}

		return "Singleplayer";
	}

	public boolean isKeyDown(int key)
	{
		return InputUtil.isKeyPressed(getMinecraft().getWindow().getHandle(), key);
	}
}