package de.pewpewproject.lasertag.client.screen.widget;

import net.minecraft.client.gui.Drawable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A column definition of a column in a list widget
 *
 * @param <T> The type of the values in the list
 * @param <R> The type of the id of the values in the list
 *
 * @author Étienne Muser
 */
public class ListColumn<T, R> {

    /**
     * Cell template cache
     * key: The cell id
     * value: The cell template
     */
    private final Map<R, Drawable> cellDrawablesCache;

    private final Function<ListCell<T>, Drawable> cellTemplate;
    private final Function<T, R> idGetter;
    private final int ratio;

    /**
     * Create a column definition
     *
     * @param cellTemplate Function to build a cell template based on a cell description
     * @param idGetter Fuction to get the id from a value
     * @param ratio The ratio how wide this column is in comparison to all the other columns
     */
    public ListColumn(Function<ListCell<T>, Drawable> cellTemplate, Function<T, R> idGetter, int ratio) {
        this.cellDrawablesCache = new HashMap<>();

        this.cellTemplate = cellTemplate;
        this.idGetter = idGetter;
        this.ratio = ratio;
    }

    /**
     * Get the cell template for a cell in this column. Caches the created cell templates.
     *
     * @param cell The cell description of the cell
     * @return The cell template
     */
    public Drawable getCellTemplate(ListCell<T> cell) {
        var id = this.idGetter.apply(cell.value());
        var cachedDrawable = this.cellDrawablesCache.get(id);

        if (cachedDrawable != null) {
            return cachedDrawable;
        }

        var drawable = cellTemplate.apply(cell);
        this.cellDrawablesCache.put(id, drawable);
        return drawable;
    }

    /**
     * Get a cell template from the cache based on the cells id
     *
     * @param id The id of the cell
     * @return The cell template
     */
    public Drawable getCellTemplate(R id) {
        return this.cellDrawablesCache.get(id);
    }

    /**
     * Get the ratio of this column
     *
     * @return The ratio
     */
    public int getRatio() {
        return this.ratio;
    }

    /**
     * Get all entries from the cell template cache
     *
     * @return The cell templates and their ids
     */
    public Set<Map.Entry<R, Drawable>> getDrawableEntries() {
        return this.cellDrawablesCache.entrySet();
    }

    /**
     * Get all cell templates as a drawables stream
     *
     * @return The stream of drawables
     */
    public Stream<Drawable> getDrawables() {
        return this.cellDrawablesCache.values().stream();
    }

    /**
     * Clears the cell template cache
     */
    public void reset() {
        this.cellDrawablesCache.clear();
    }
}
