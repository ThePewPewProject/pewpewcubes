package de.pewpewproject.lasertag.common.util;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Helper class extending the base drawing functionality of Minecraft's DrawableHelper
 *
 * @author Étienne Muser
 */
public abstract class AdvancedDrawableHelper extends DrawableHelper {
    /**
     * Draw a non-filled rectangle on the matrix stack
     * @param matrices The matrix stack
     * @param startX The x-coordinate where to start the rectangle
     * @param startY The y-coordinate where to start the rectangle
     * @param endX The x-coordinate where to end the rectangle
     * @param endY The y-coordinate where to end the rectangle
     * @param color The color of the rectangle
     */
    protected void drawRectangle(MatrixStack matrices, int startX, int startY, int endX, int endY, int color) {
        // Draw the left line
        drawVerticalLine(matrices, startX, startY - 1, endY, color);

        // Draw the top line
        drawHorizontalLine(matrices, startX + 1, endX, startY, color);

        // Draw the right line
        drawVerticalLine(matrices, endX, startY, endY + 1, color);

        // Draw the bottom line
        drawHorizontalLine(matrices, startX, endX - 1, endY, color);
    }
}
