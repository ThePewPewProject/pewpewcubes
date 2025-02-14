package de.pewpewproject.lasertag.client.screen.widget.list.grouped;

import de.pewpewproject.lasertag.client.screen.widget.list.ListCell;
import de.pewpewproject.lasertag.client.screen.widget.list.ListColumn;
import de.pewpewproject.lasertag.client.screen.widget.list.ListColumnsDefinition;
import de.pewpewproject.lasertag.client.screen.widget.list.ListWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.ParentElement;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A grouped list widget
 *
 * @author Étienne Muser
 */
public class GroupedListWidget<T> extends ListWidget<GroupedListElement<T>, UUID> {

    /**
     * Creates a list widget given an available height. Calculates how many rows are visible and the actual
     * height of the widget based on the available height and the fixed row height of 22. Buttons can
     * only be max 22 high.
     *
     * @param x                          The start x-position
     * @param y                          The start y-position
     * @param width                      The width of the widget
     * @param availableHeight            The available height
     * @param dataSource                 Function to get the data for this list
     * @param columnsDefinition          The column definitions for this list
     * @param groupBy                    Function to get the value to group by
     * @param groupingHeaderCellTemplate BiFunction to get the group header cell template
     * @param nameGetter                 Function to get the name of a value for building the id later
     * @param parent                     The parent of the list
     * @param textRenderer               The text renderer used to render text
     * @param <T>                        The type of the values
     * @param <G>                        The tpye of the grouped property
     * @return The list widget
     */
    public static <T, G> GroupedListWidget<T> fromAvailableHeight(int x,
                                                                  int y,
                                                                  int width,
                                                                  int availableHeight,
                                                                  Supplier<List<T>> dataSource,
                                                                  ListColumnsDefinition<T, UUID> columnsDefinition,
                                                                  Function<T, G> groupBy,
                                                                  BiFunction<G, ListCell<?>, Drawable> groupingHeaderCellTemplate,
                                                                  Function<T, String> nameGetter,
                                                                  ParentElement parent,
                                                                  TextRenderer textRenderer) {
        var rowHeight = 22;
        // Integer division intended
        var numberOfVisibleElements = availableHeight / rowHeight;
        var calculatedHeight = rowHeight * numberOfVisibleElements;

        return GroupedListWidget.of(x,
                y,
                width,
                calculatedHeight,
                rowHeight,
                numberOfVisibleElements,
                dataSource,
                columnsDefinition,
                groupBy,
                groupingHeaderCellTemplate,
                nameGetter,
                parent,
                textRenderer);
    }

    /**
     * Creates a list widget
     *
     * @param x                    The start x-position
     * @param y                    The start y-position
     * @param width                The width of the widget
     * @param height               The height of the widget
     * @param rowHeight            The height of the rows
     * @param numberOfVisibleItems The number of visible rows
     * @param dataSource           Function to get the data for this list
     * @param columnsDefinition    The column definitions for this list
     * @param parent               The parent of the list
     * @param textRenderer         The text renderer used to render text
     */
    public GroupedListWidget(int x, int y, int width, int height, int rowHeight, int numberOfVisibleItems, Supplier<List<GroupedListElement<T>>> dataSource, ListColumnsDefinition<GroupedListElement<T>, UUID> columnsDefinition, ParentElement parent, TextRenderer textRenderer) {
        super(x, y, width, height, rowHeight, numberOfVisibleItems, dataSource, columnsDefinition, parent, textRenderer);
    }

    public static <T, G> GroupedListWidget<T> of(int x,
                                                 int y,
                                                 int width,
                                                 int height,
                                                 int rowHeight,
                                                 int numberOfVisibleItems,
                                                 Supplier<List<T>> dataSource,
                                                 ListColumnsDefinition<T, UUID> columnsDefinition,
                                                 Function<T, G> groupBy,
                                                 BiFunction<G, ListCell<?>, Drawable> groupingHeaderCellTemplate,
                                                 Function<T, String> nameGetter,
                                                 ParentElement parent,
                                                 TextRenderer textRenderer) {

        Supplier<List<GroupedListElement<T>>> groupedDataSource = () -> {
            var grouped = dataSource.get().stream().collect(Collectors.groupingBy(groupBy));

            var list = new LinkedList<GroupedListElement<T>>();

            grouped.forEach((k, v) -> {

                if (grouped.size() > 1) {
                    list.add(new GroupedListGroupElement<>(k, groupingHeaderCellTemplate));
                }

                v.forEach(val -> list.add(new GroupedListValueElement<>(val)));
            });

            return list;
        };

        var groupedColumns = new LinkedList<ListColumn<GroupedListElement<T>, UUID>>();

        var columnIndex = new AtomicInteger(0);

        columnsDefinition.columns().forEach(c -> {
            var colIndex = columnIndex.getAndIncrement();

            Function<ListCell<GroupedListElement<T>>, Drawable> cellTemplate = (cell) ->
                    cell.value().getTemplate(cell, c.getCellTemplate(), colIndex == 0);

            Function<GroupedListElement<T>, UUID> idGetter = (val) -> val.getId(nameGetter);

            groupedColumns.add(new ListColumn<>(cellTemplate, idGetter, c.getRatio()));
        });

        var groupedColumnsDefinition = new ListColumnsDefinition<>(groupedColumns);

        return new GroupedListWidget<>(x, y, width, height, rowHeight, numberOfVisibleItems, groupedDataSource, groupedColumnsDefinition, parent, textRenderer);
    }

}
