package de.pewpewproject.lasertag.client.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Supplier;

/**
 * A complex list widget with caching of the cell contents and scrolling. Mouse scroll events must be forwarded
 * to the list for the scrolling to work. Mouse dragged events must be forwarded for the scrollbar to work.
 * The tick event must be forwarded if the list contains text field widgets. The mouse click event must be
 * forwarded to text field widgets to work.
 *
 * @param <T> The type of the values
 * @param <R> The type of the id of the values
 *
 * @author Étienne Muser
 */
public class ListWidget<T, R> extends DrawableHelper implements Drawable, Element, Selectable {

    private static final int ROW_COLOR = 0x50FFFFFF;
    private static final int ALT_ROW_COLOR = 0x80000000;

    private static final int SCROLLBAR_WIDTH = 5;
    private static final int SCROLLBAR_PADDING = 2;
    private static final int SCROLLBAR_BACKGROUND_COLOR = 0x80000000;
    private static final int SCROLLBAR_FOREGROUND_COLOR = 0xFFFFFFFF;

    private final Supplier<List<T>> dataSource;
    private final ListColumnsDefinition<T, R> columnsDefinition;

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int rowHeight;
    private final int numberOfVisibleItems;

    private int indexOfFirstVisibleItem = 0;

    private boolean scrolling = false;

    private final ParentElement parent;
    private final TextRenderer textRenderer;

    private ListColumn<T, R> lastFocusedColumn = null;
    private R lastFocusedElementId = null;
    private boolean restoreFocusNecessary = false;

    /**
     * Creates a list widget given an available height. Calculates how many rows are visible and the actual
     * height of the widget based on the available height and the fixed row height of 22. Buttons can
     * only be max 22 high.
     *
     * @param x The start x-position
     * @param y The start y-position
     * @param width The width of the widget
     * @param availableHeight The available height
     * @param dataSource Function to get the data for this list
     * @param columnsDefinition The column definitions for this list
     * @param parent The parent of the list
     * @param textRenderer The text renderer used to render text
     * @return The list widget
     * @param <T> The type of the values
     * @param <R> The type of the id of the values
     */
    public static <T, R> ListWidget<T, R> fromAvailableHeight(int x, int y,
                                                              int width, int availableHeight,
                                                              Supplier<List<T>> dataSource, ListColumnsDefinition<T, R> columnsDefinition,
                                                              ParentElement parent, TextRenderer textRenderer) {
        var rowHeight = 22;
        // Integer division intended
        var numberOfVisibleElements = availableHeight / rowHeight;
        var calculatedHeight = rowHeight * numberOfVisibleElements;

        return new ListWidget<>(x, y,
                width, calculatedHeight,
                rowHeight, numberOfVisibleElements,
                dataSource, columnsDefinition,
                parent, textRenderer);
    }

    /**
     * Creates a list widget
     *
     * @param x The start x-position
     * @param y The start y-position
     * @param width The width of the widget
     * @param height The height of the widget
     * @param rowHeight The height of the rows
     * @param numberOfVisibleItems The number of visible rows
     * @param dataSource Function to get the data for this list
     * @param columnsDefinition The column definitions for this list
     * @param parent The parent of the list
     * @param textRenderer The text renderer used to render text
     */
    public ListWidget(int x, int y,
                      int width, int height,
                      int rowHeight, int numberOfVisibleItems,
                      Supplier<List<T>> dataSource, ListColumnsDefinition<T, R> columnsDefinition,
                      ParentElement parent, TextRenderer textRenderer) {
        this.dataSource = dataSource;
        this.columnsDefinition = columnsDefinition;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rowHeight = rowHeight;
        this.numberOfVisibleItems = numberOfVisibleItems;
        this.parent = parent;
        this.textRenderer = textRenderer;
    }

    /**
     * Reload the data source of the list
     */
    public void refreshDataSource() {

        synchronized (this) {

            this.columnsDefinition.columns().forEach(ListColumn::reset);

            this.restoreFocusNecessary = true;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        this.renderList(matrices, mouseX, mouseY, delta);
        this.renderScrollbar(matrices);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.FOCUSED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        // Do nothing
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.indexOfFirstVisibleItem = MathHelper.clamp(this.indexOfFirstVisibleItem - (int) amount, 0, this.getMaxFirstElement());
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (mouseX < this.x || mouseX > this.x + this.width || mouseY < this.y || mouseY > this.y + this.height) {
            return false;
        }

        this.scrolling = mouseX >= this.x + this.width - SCROLLBAR_WIDTH && mouseX < this.x + this.width;

        if (this.scrolling) {
            doMouseScoll(mouseY);
        }

        var listYOffset = this.indexOfFirstVisibleItem * this.rowHeight;
        mouseY += listYOffset;

        double finalMouseY = mouseY;

        var result = false;
        for (var column : this.columnsDefinition.columns()) {

            for (var drawableEntry : column.getDrawableEntries()) {

                // Cast to element
                if (drawableEntry.getValue() instanceof Element element) {
                    if (element.mouseClicked(mouseX, finalMouseY, button)) {
                        parent.setFocused(element);
                        this.lastFocusedColumn = column;
                        this.lastFocusedElementId = drawableEntry.getKey();
                        result = true;
                    }
                }
            }
        }

        if (!result) {
            this.lastFocusedColumn = null;
            this.lastFocusedElementId = null;
        }

        return result;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.scrolling || button != 0) {
            return false;
        }

        doMouseScoll(mouseY);

        return true;
    }

    public void tick() {
        this.columnsDefinition.columns().stream()
                .flatMap(ListColumn::getDrawables)
                .filter(drawable -> drawable instanceof TextFieldWidget)
                .map(drawable -> (TextFieldWidget) drawable)
                .forEach(TextFieldWidget::tick);
    }

    private void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        // Get the list data
        var data = dataSource.get();

        if (data.isEmpty()) {
            this.renderEmptyList(matrices);
            return;
        }

        matrices.push();

        var listYOffset = this.indexOfFirstVisibleItem * this.rowHeight;
        mouseY += listYOffset;
        matrices.translate(0, -listYOffset, 0);

        // Sum the ratios of each column
        var ratioSum = columnsDefinition.columns().stream().map(ListColumn::getRatio).reduce(Integer::sum).get();

        synchronized (this) {
            int startX = this.x;
            for (var columnDefinition : columnsDefinition.columns()) {

                // Calculate the column width
                var columnWidth = (int) (this.width * ((float) columnDefinition.getRatio() / (float) ratioSum)) - (SCROLLBAR_WIDTH + SCROLLBAR_PADDING);

                int index = 0;
                for (var dataItem : data) {

                    var cellStartY = this.y + (index * rowHeight);

                    // Build the list cell
                    var listCell = new ListCell<T>(startX, cellStartY, columnWidth, rowHeight, dataItem);

                    // Get the cell drawable
                    var cellDrawable = columnDefinition.getCellTemplate(listCell);

                    if (index >= this.indexOfFirstVisibleItem && index < this.indexOfFirstVisibleItem + this.numberOfVisibleItems) {
                        // Draw background
                        DrawableHelper.fill(matrices, startX, cellStartY, startX + columnWidth, cellStartY + rowHeight, index % 2 == 0 ? ROW_COLOR : ALT_ROW_COLOR);

                        // Draw cell template
                        cellDrawable.render(matrices, mouseX, mouseY, delta);
                    }

                    ++index;
                }

                startX += columnWidth;
            }
        }

        if (this.restoreFocusNecessary) {
            this.restoreFocusNecessary = false;
            restoreFocusIfPossible();
        }

        matrices.pop();
    }

    private void renderScrollbar(MatrixStack matrices) {

        if (this.dataSource.get().size() <= this.numberOfVisibleItems) {
            return;
        }

        var scrollbarStartX = this.x + this.width - SCROLLBAR_WIDTH;

        // Draw background
        DrawableHelper.fill(matrices, scrollbarStartX, this.y, scrollbarStartX + SCROLLBAR_WIDTH, this.y + this.height, SCROLLBAR_BACKGROUND_COLOR);

        var dataSourceSize = dataSource.get().size();

        // Calculate the scrollbar height
        var scrollbarHeight = this.getScrollbarHeight();

        // Calculate the height of one element
        var oneElementHeight = this.height * (1.0F / dataSourceSize);

        // Calculate the start height of the scrollbar
        var scrollbarStartY = (int) (this.y + (this.indexOfFirstVisibleItem * oneElementHeight));

        // Draw Foreground
        DrawableHelper.fill(matrices, scrollbarStartX, scrollbarStartY, scrollbarStartX + SCROLLBAR_WIDTH, scrollbarStartY + scrollbarHeight, SCROLLBAR_FOREGROUND_COLOR);
    }

    private int getMaxFirstElement() {
        var dataSourceSize = dataSource.get().size();

        return Math.max(0, dataSourceSize - this.numberOfVisibleItems);
    }

    private int getScrollbarHeight() {
        return (int) (this.height * (this.numberOfVisibleItems / (float) dataSource.get().size()));
    }

    private void doMouseScoll(double mouseY) {

        // Calculate the height of one element
        var oneElementHeight = this.height * (1.0 / dataSource.get().size());

        var halfScrollbarHeight = this.getScrollbarHeight() / 2.0;

        var newScrollbarStartHeight = mouseY - halfScrollbarHeight;

        // Integer division intended
        this.indexOfFirstVisibleItem = MathHelper.clamp((int) (newScrollbarStartHeight - this.y) / (int) oneElementHeight, 0, this.getMaxFirstElement());
    }

    private void restoreFocusIfPossible() {
        if (this.lastFocusedColumn != null) {

            // Restore focus
            var element = (Element) this.lastFocusedColumn.getCellTemplate(this.lastFocusedElementId);

            if (element == null) {
                return;
            }

            element.changeFocus(true);
            parent.setFocused(element);
        }
    }

    private void renderEmptyList(MatrixStack matrices) {
        DrawableHelper.fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, ALT_ROW_COLOR);
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, Text.translatable("gui.list_widget.empty"), this.x + this.width / 2, this.y + this.height / 2, 0xFFFFFFFF);
    }
}
