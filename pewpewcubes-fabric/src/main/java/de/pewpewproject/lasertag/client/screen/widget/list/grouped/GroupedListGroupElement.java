package de.pewpewproject.lasertag.client.screen.widget.list.grouped;

import de.pewpewproject.lasertag.client.screen.widget.list.ListCell;
import net.minecraft.client.gui.Drawable;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Implementation of a grouped list element for group headers
 *
 * @author Étienne Muser
 */
public final class GroupedListGroupElement<T, G> extends GroupedListElement<T> {

    private final G groupingHeader;

    private final BiFunction<G, ListCell<?>, Drawable> groupingHeaderCellTemplate;

    private final UUID id;

    public GroupedListGroupElement(G groupingHeader, BiFunction<G, ListCell<?>, Drawable> groupingHeaderCellTemplate) {
        this.groupingHeader = groupingHeader;
        this.groupingHeaderCellTemplate = groupingHeaderCellTemplate;
        this.id = UUID.randomUUID();
    }

    @Override
    public Drawable getTemplate(ListCell<GroupedListElement<T>> cell, Function<ListCell<T>, Drawable> cellTemplate, boolean firstColumn) {
        return firstColumn ? groupingHeaderCellTemplate.apply(groupingHeader, cell) : (a, b, c, d) -> {};
    }

    @Override
    public UUID getId(Function<T, String> nameGetter) {
        return id;
    }
}
