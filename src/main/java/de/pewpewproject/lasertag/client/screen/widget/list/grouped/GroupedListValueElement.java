package de.pewpewproject.lasertag.client.screen.widget.list.grouped;

import de.pewpewproject.lasertag.client.screen.widget.list.ListCell;
import net.minecraft.client.gui.Drawable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

/**
 * Implementation of a grouped list element vor value elements
 *
 * @author Étienne Muser
 */
public final class GroupedListValueElement<T> extends GroupedListElement<T> {

    private final T value;

    public GroupedListValueElement(T value) {
        this.value = value;
    }

    @Override
    public Drawable getTemplate(ListCell<GroupedListElement<T>> cell, Function<ListCell<T>, Drawable> cellTemplate, boolean firstColumn) {
        return cellTemplate.apply(new ListCell<>(cell.x(), cell.y(), cell.width(), cell.height(), value));
    }

    @Override
    public UUID getId(Function<T, String> nameGetter) {
        return UUID.nameUUIDFromBytes(nameGetter.apply(value).getBytes(StandardCharsets.UTF_8));
    }
}
