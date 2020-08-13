package dev.latvian.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class KubeJSClientEventHandler {
	public static final Event<Runnable> ON_JOIN = EventFactory.createArrayBacked(Runnable.class, callbacks -> () -> {
		for (Runnable callback : callbacks) {
			callback.run();
		}
	});
	public static final Event<Runnable> ON_LOGOUT = EventFactory.createArrayBacked(Runnable.class, callbacks -> () -> {
		for (Runnable callback : callbacks) {
			callback.run();
		}
	});
	public static final Event<Consumer<ClientPlayerEntity>> ON_RESPAWN = EventFactory.createArrayBacked(Consumer.class, callbacks -> (player) -> {
		for (Consumer<ClientPlayerEntity> callback : callbacks) {
			callback.accept(player);
		}
	});
	
	public static final Event<BiConsumer<Boolean, List<String>>> ON_DEBUG_TEXT = EventFactory.createArrayBacked(BiConsumer.class, callbacks -> (left, lines) -> {
		for (BiConsumer<Boolean, List<String>> callback : callbacks) {
			callback.accept(left, lines);
		}
	});
	private static FieldJS<List<AbstractButtonWidget>> buttons;
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	
	// TODO
	public void init() {
		AfterScriptLoadCallback.EVENT.register(this::setup);
		ItemTooltipCallback.EVENT.register(this::itemTooltip);
		ClientTickEvents.START_CLIENT_TICK.register(this::clientTick);
		ON_JOIN.register(this::loggedIn);
		ON_LOGOUT.register(this::loggedOut);
		ON_RESPAWN.register(this::respawn);
		HudRenderCallback.EVENT.register(this::inGameScreenDraw);
		ON_DEBUG_TEXT.register((left, lines) -> {
			if (left) debugLeftInfo(lines);
			else debugRightInfo(lines);
		});
//		MinecraftForge.EVENT_BUS.addListener(this::guiScreenDraw);
//		MinecraftForge.EVENT_BUS.addListener(this::guiPostInit);
		AfterScriptLoadCallback.EVENT.register(this::itemColors);
		AfterScriptLoadCallback.EVENT.register(this::blockColors);
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postAtlasStitch);
	}
	
	public void setup() {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			switch (builder.renderType) {
				case "cutout":
					BlockRenderLayerMap.INSTANCE.putBlock(builder.block, RenderLayer.getCutout());
					break;
				case "cutout_mipped":
					BlockRenderLayerMap.INSTANCE.putBlock(builder.block, RenderLayer.getCutoutMipped());
					break;
				case "translucent":
					BlockRenderLayerMap.INSTANCE.putBlock(builder.block, RenderLayer.getTranslucent());
					break;
				//default:
				//	RenderTypeLookup.setRenderLayer(block, RenderType.getSolid());
			}
		}
	}
	
	private void debugLeftInfo(List<String> left) {
		if (MinecraftClient.getInstance().player != null) {
			new DebugInfoEventJS(left).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO_LEFT);
		}
	}
	
	private void debugRightInfo(List<String> right) {
		if (MinecraftClient.getInstance().player != null) {
			new DebugInfoEventJS(right).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO_RIGHT);
		}
	}
	
	private void itemTooltip(ItemStack stack, TooltipContext context, List<net.minecraft.text.Text> lines) {
		if (ClientProperties.get().showTagNames && MinecraftClient.getInstance().options.advancedItemTooltips && Screen.hasShiftDown()) //hasShiftDown
		{
			for (Identifier tag : MinecraftClient.getInstance().world.getTagManager().getItems().getTagsFor(stack.getItem())) {
				lines.add(new LiteralText(" #" + tag).formatted(Formatting.DARK_GRAY));
			}
		}
		
		new ClientItemTooltipEventJS(stack, context, lines).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_ITEM_TOOLTIP);
	}
	
	private void clientTick(MinecraftClient client) {
		if (client.player != null) {
			new ClientTickEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_TICK);
		}
	}
	
	private void loggedIn() {
		ClientWorldJS.instance = new ClientWorldJS(MinecraftClient.getInstance(), MinecraftClient.getInstance().player);
		AttachWorldDataEvent.EVENT.invoker().accept(new AttachWorldDataEvent(ClientWorldJS.instance));
		AttachPlayerDataEvent.EVENT.invoker().accept(new AttachPlayerDataEvent(ClientWorldJS.instance.clientPlayerData));
		new ClientLoggedInEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_IN);
	}
	
	private void loggedOut() {
		if (ClientWorldJS.instance != null) {
			new ClientLoggedInEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_OUT);
		}
		
		ClientWorldJS.instance = null;
		KubeJSClient.activeOverlays.clear();
	}
	
	private void respawn(ClientPlayerEntity entity) {
		ClientWorldJS.instance = new ClientWorldJS(MinecraftClient.getInstance(), entity);
		AttachWorldDataEvent.EVENT.invoker().accept(new AttachWorldDataEvent(ClientWorldJS.instance));
		AttachPlayerDataEvent.EVENT.invoker().accept(new AttachPlayerDataEvent(ClientWorldJS.instance.clientPlayerData));
	}
	
	private int drawOverlay(MinecraftClient mc, MatrixStack matrixStack, int maxWidth, int x, int y, int p, Overlay o, boolean inv) {
		List<OrderedText> list = new ArrayList<>();
		int l = 10;
		
		for (Text t : o.text) {
			list.addAll(mc.textRenderer.wrapLines(t.component(), maxWidth));
		}
		
		int mw = 0;
		
		for (OrderedText s : list) {
			mw = Math.max(mw, mc.textRenderer.getWidth(s));
		}
		
		if (mw == 0) {
			return 0;
		}
		
		int w = mw + p * 2;
		int h = list.size() * l + p * 2 - 2;
		int col = 0xFF000000 | o.color;
		int r = (col >> 16) & 0xFF;
		int g = (col >> 8) & 0xFF;
		int b = col & 0xFF;
		
		RenderSystem.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		
		//o.color.withAlpha(200).draw(spx, spy, mw + p * 2, list.size() * l + p * 2 - 2);
		
		if (inv) {
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 255);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y, w, 1, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, 0, 0, 0, 80);
		} else {
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 200);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x, y, w, 1, r, g, b, 255);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, r, g, b, 255);
		}
		
		tessellator.draw();
		RenderSystem.enableTexture();
		
		for (int i = 0; i < list.size(); i++) {
			mc.textRenderer.drawWithShadow(matrixStack, list.get(i), x + p, y + i * l + p, 0xFFFFFFFF);
		}
		
		return list.size() * l + p * 2 + (p - 2);
	}
	
	
	private void addRectToBuffer(BufferBuilder buffer, int x, int y, int w, int h, int r, int g, int b, int a) {
		buffer.vertex(x, y + h, 0D).color(r, g, b, a).next();
		buffer.vertex(x + w, y + h, 0D).color(r, g, b, a).next();
		buffer.vertex(x + w, y, 0D).color(r, g, b, a).next();
		buffer.vertex(x, y, 0D).color(r, g, b, a).next();
	}
	
	private void inGameScreenDraw(MatrixStack matrices, float delta) {
		if (KubeJSClient.activeOverlays.isEmpty()) {
			return;
		}
		
		MinecraftClient mc = MinecraftClient.getInstance();
		
		if (mc.options.debugEnabled || mc.currentScreen != null) {
			return;
		}
		
		matrices.push();
		matrices.translate(0, 0, 800);
		
		RenderSystem.enableBlend();
		RenderSystem.disableLighting();
		
		int maxWidth = mc.getWindow().getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;
		
		for (Overlay o : KubeJSClient.activeOverlays.values()) {
			spy += drawOverlay(mc, matrices, maxWidth, spx, spy, p, o, false);
		}
		matrices.pop();
	}
	
	//
//	private void guiScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event)
//	{
//		if (KubeJSClient.activeOverlays.isEmpty())
//		{
//			return;
//		}
//
//		Minecraft mc = MinecraftClient.getInstance();
//		MatrixStack matrixStack = new MatrixStack();
//		matrixStack.translate(0, 0, 800);
//
//		RenderSystem.enableBlend();
//		RenderSystem.disableLighting();
//
//		int maxWidth = mc.getWindow().getScaledWidth() / 4;
//		int p = 4;
//		int spx = p;
//		int spy = p;
//
//		if (buttons == null)
//		{
//			buttons = UtilsJS.getField(Screen.class, "buttons");
//		}
//
//		while (isOver(buttons.get(event.getGui()).orElse(Collections.emptyList()), spx, spy))
//		{
//			spy += 16;
//		}
//
//		for (Overlay o : KubeJSClient.activeOverlays.values())
//		{
//			if (o.alwaysOnTop)
//			{
//				spy += drawOverlay(mc, matrixStack, maxWidth, spx, spy, p, o, true);
//			}
//		}
//	}
//
//	private boolean isOver(List<Widget> list, int x, int y)
//	{
//		for (Widget w : list)
//		{
//			if (w.field_230694_p_ && x >= w.field_230690_l_ && y >= w.field_230691_m_ && x < w.field_230690_l_ + w.func_230998_h_() && y < w.field_230691_m_ + w.getHeight()) //TODO: visible, width
//			{
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	private void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
//	{
//		if (ClientProperties.get().disableRecipeBook && event.getGui() instanceof IRecipeShownListener)
//		{
//			for (Widget widget : event.getWidgetList())
//			{
//				if (widget instanceof ImageButtonKJS && RECIPE_BUTTON_TEXTURE.equals(((ImageButtonKJS) widget).getButtonTextureKJS()))
//				{
//					event.removeWidget(widget);
//					return;
//				}
//			}
//		}
//	}
//
	private void itemColors() {
		for (ItemBuilder builder : KubeJSObjects.ITEMS.values()) {
			if (!builder.color.isEmpty()) {
				ColorProviderRegistry.ITEM.register((stack, index) -> builder.color.get(index), builder.item);
			}
		}
		
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			if (builder.itemBuilder != null && !builder.color.isEmpty()) {
				ColorProviderRegistry.ITEM.register((stack, index) -> builder.color.get(index), builder.itemBuilder.blockItem);
			}
		}

//		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
//		{
//			if (builder.stillFluid.getAttributes().getColor() != 0xFFFFFFFF)
//			{
//				event.getItemColors().register((stack, index) -> index == 1 ? builder.stillFluid.getAttributes().getColor() : 0xFFFFFFFF, builder.bucketItem);
//			}
//		}
	}
	
	private void blockColors() {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			if (!builder.color.isEmpty()) {
				ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> builder.color.get(index), builder.block);
			}
		}
	}
//
//	private void postAtlasStitch(TextureStitchEvent.Post event)
//	{
//		if (!ClientProperties.get().exportAtlases)
//		{
//			return;
//		}
//
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, event.getMap().getGlTextureId());
//		int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
//		int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
//
//		if (w <= 0 || h <= 0)
//		{
//			return;
//		}
//
//		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//		int[] pixels = new int[w * h];
//
//		IntBuffer result = BufferUtils.createIntBuffer(w * h);
//		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, result);
//		result.get(pixels);
//
//		image.setRGB(0, 0, w, h, pixels, 0, w);
//
//		Path path = FMLPaths.GAMEDIR.get().resolve("kubejs/exported/" + event.getMap().getTextureLocation().getNamespace() + "/" + event.getMap().getTextureLocation().getPath());
//
//		if (!Files.exists(path.getParent()))
//		{
//			try
//			{
//				Files.createDirectories(path.getParent());
//			}
//			catch (Exception ex)
//			{
//				ex.printStackTrace();
//				return;
//			}
//		}
//
//		if (!Files.exists(path))
//		{
//			try
//			{
//				Files.createFile(path);
//			}
//			catch (Exception ex)
//			{
//				ex.printStackTrace();
//				return;
//			}
//		}
//
//		try (OutputStream stream = Files.newOutputStream(path))
//		{
//			ImageIO.write(image, "PNG", stream);
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//		}
//	}
}