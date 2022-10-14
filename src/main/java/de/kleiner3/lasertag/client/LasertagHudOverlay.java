package de.kleiner3.lasertag.client;

import de.kleiner3.lasertag.types.Colors;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class LasertagHudOverlay implements HudRenderCallback {

	@Override
	public void onHudRender(MatrixStack matrixStack, float tickDelta) {

		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) {
			return;
		}
		
		int width = client.getWindow().getScaledWidth();
		int height = client.getWindow().getScaledHeight();
		
		int x = width / 2;
		int y = height;
		
		TextRenderer textRenderer = client.textRenderer;
		
		DrawableHelper.drawCenteredText(matrixStack, textRenderer, "Laserag", x, y, Colors.RED.getValue());
	}

}
