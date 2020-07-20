package dev.latvian.kubejs.player;

import dev.latvian.kubejs.core.PlayerInteractionManagerKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageCloseOverlay;
import dev.latvian.kubejs.net.MessageOpenOverlay;
import dev.latvian.kubejs.net.MessageSendDataFromServer;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<ServerPlayerEntity>
{
	public final ServerJS server;
	private final boolean hasClientMod;

	public ServerPlayerJS(ServerPlayerDataJS d, ServerWorldJS w, ServerPlayerEntity p)
	{
		super(d, w, p);
		server = w.getServer();
		hasClientMod = d.hasClientMod();
	}

	@Override
	public PlayerStatsJS getStats()
	{
		return new PlayerStatsJS(this, minecraftPlayer.getStatHandler());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		KubeJSNet.sendToPlayers(Collections.singletonList(minecraftPlayer), new MessageOpenOverlay(overlay));
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJSNet.sendToPlayers(Collections.singletonList(minecraftPlayer), new MessageCloseOverlay(overlay));
	}

	@Override
	public boolean isMiningBlock()
	{
		return ((PlayerInteractionManagerKJS) minecraftPlayer.interactionManager).isDestroyingBlockKJS();
	}

	public void setCreativeMode(boolean mode)
	{
		minecraftPlayer.interactionManager.method_30118(mode ? GameMode.CREATIVE : GameMode.SURVIVAL);
	}

	public void setGameMode(String mode)
	{
		if (mode.equals("survival"))
		{
			minecraftPlayer.interactionManager.method_30118(GameMode.SURVIVAL);
		}
		else if (mode.equals("creative"))
		{
			minecraftPlayer.interactionManager.method_30118(GameMode.CREATIVE);
		}
		else if (mode.equals("adventure"))
		{
			minecraftPlayer.interactionManager.method_30118(GameMode.ADVENTURE);
		}
		else if (mode.equals("spectator"))
		{
			minecraftPlayer.interactionManager.method_30118(GameMode.SPECTATOR);
		}
	}


	public boolean isOP()
	{
		return server.minecraftServer.getPlayerManager().isOperator(minecraftPlayer.getGameProfile());
	}

	public void kick(Object reason)
	{
		minecraftPlayer.networkHandler.disconnect(Text.of(reason).component());
	}

	public void kick()
	{
		kick(new TextTranslate("multiplayer.disconnect.kicked"));
	}

	public void ban(String banner, String reason, long expiresInMillis)
	{
		Date date = new Date();
		BannedPlayerEntry userlistbansentry = new BannedPlayerEntry(minecraftPlayer.getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.minecraftServer.getPlayerManager().getUserBanList().add(userlistbansentry);
		kick(new TextTranslate("multiplayer.disconnect.banned"));
	}

	public boolean hasClientMod()
	{
		return hasClientMod;
	}

	public void unlockAdvancement(@ID String id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancementTracker().getProgress(a.advancement);

			for (String s : advancementprogress.getUnobtainedCriteria())
			{
				minecraftPlayer.getAdvancementTracker().grantCriterion(a.advancement, s);
			}
		}
	}

	public void revokeAdvancement(@ID String id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancementTracker().getProgress(a.advancement);

			if (advancementprogress.isAnyObtained())
			{
				for (String s : advancementprogress.getObtainedCriteria())
				{
					minecraftPlayer.getAdvancementTracker().revokeCriterion(a.advancement, s);
				}
			}
		}
	}

	@Override
	public void setSelectedSlot(int index)
	{
		int p = getSelectedSlot();
		super.setSelectedSlot(index);
		int n = getSelectedSlot();

		if (p != n && minecraftPlayer.networkHandler != null)
		{
			minecraftPlayer.networkHandler.sendPacket(new HeldItemChangeS2CPacket(n));
		}
	}

	@Override
	public void setMouseItem(Object item)
	{
		super.setMouseItem(item);

		if (minecraftPlayer.networkHandler != null)
		{
			minecraftPlayer.updateCursorStack();
		}
	}

	@Override
	public void sendData(String channel, @Nullable Object data)
	{
		if (!channel.isEmpty())
		{
			KubeJSNet.sendToPlayers(Collections.singletonList(minecraftPlayer), new MessageSendDataFromServer(channel, MapJS.nbt(data)));
		}
	}
}