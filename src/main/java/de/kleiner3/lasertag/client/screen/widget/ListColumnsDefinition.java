package de.kleiner3.lasertag.client.screen.widget;

import java.util.List;

/**
 * The column definitions of a list widget. Wraps all columns definitions into one object.
 *
 * @param columns The column definitions
 * @param <T> The type of the value of a cell
 * @param <R> The type of the id of a value of a cell
 *
 * @author Étienne Muser
 */
public record ListColumnsDefinition<T, R>(List<ListColumn<T, R>> columns) {
}
