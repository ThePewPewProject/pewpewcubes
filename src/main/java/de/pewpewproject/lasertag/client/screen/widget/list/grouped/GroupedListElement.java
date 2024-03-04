package de.pewpewproject.lasertag.client.screen.widget.list.grouped;

import de.pewpewproject.lasertag.client.screen.widget.list.ListCell;
import net.minecraft.client.gui.Drawable;

import java.util.UUID;
import java.util.function.Function;

/**
 * Base class for a grouped list element
 *
 * @author Étienne Muser
 */
public abstract sealed class GroupedListElement<T> permits GroupedListGroupElement, GroupedListValueElement {

    public abstract Drawable getTemplate(ListCell<GroupedListElement<T>> cell, Function<ListCell<T>, Drawable> cellTemplate, boolean firstColumn);

    public abstract UUID getId(Function<T, String> nameGetter);
}
