package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Overlay;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageOpenOverlay
{
	private final Overlay overlay;

	public MessageOpenOverlay(Overlay o)
	{
		overlay = o;
	}

	public MessageOpenOverlay(PacketByteBuf buffer)
	{
		overlay = new Overlay(buffer.readString(5000));
		overlay.color = buffer.readInt();
		overlay.alwaysOnTop = buffer.readBoolean();
		int s = buffer.readUnsignedByte();

		for (int i = 0; i < s; i++)
		{
			overlay.add(Text.read(buffer));
		}
	}

	public void write(PacketByteBuf buffer)
	{
		buffer.writeString(overlay.id, 5000);
		buffer.writeInt(overlay.color);
		buffer.writeBoolean(overlay.alwaysOnTop);
		buffer.writeByte(overlay.text.size());

		for (Text t : overlay.text)
		{
			t.write(buffer);
		}
	}

	public void handle(Supplier<PacketContext> context)
	{
		context.get().getTaskQueue().execute(() -> KubeJS.instance.proxy.openOverlay(overlay));
	}
}