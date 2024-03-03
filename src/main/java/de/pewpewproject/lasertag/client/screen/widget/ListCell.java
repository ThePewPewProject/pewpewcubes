package de.pewpewproject.lasertag.client.screen.widget;

/**
 * A cell description for a cell in a list widget
 *
 * @param x The start x-position of the cell
 * @param y The start y-position of the cell
 * @param width The width of the cell
 * @param height The height of the cell
 * @param value The value of this cell
 * @param <T> The type of the value
 *
 * @author Étienne Muser
 */
public record ListCell<T>(int x, int y, int width, int height, T value) {
}
