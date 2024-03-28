package de.pewpewproject.lasertag.client.screen.widget.list;

import de.pewpewproject.lasertag.client.screen.widget.ITooltipHolding;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
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

    private Integer lastFocusedColumnIndex = null;
    private R lastFocusedElementId = null;
    private Drawable hoveredCell = null;
    private boolean restoreFocusNecessary = false;

    /**
     * Creates a list widget given an available height. Calculates how many rows are visible and the actual
     * height of the widget based on the available height and the fixed row height of 22. Buttons can
     * only be max 22 high.
     *
     * @param x                 The start x-position
     * @param y                 The start y-position
     * @param width             The width of the widget
     * @param availableHeight   The available height
     * @param dataSource        Function to get the data for this list
     * @param columnsDefinition The column definitions for this list
     * @param parent            The parent of the list
     * @param textRenderer      The text renderer used to render text
     * @param <T>               The type of the values
     * @param <R>               The type of the id of the values
     * @return The list widget
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
    public synchronized void refreshDataSource() {
        columnsDefinition.columns().forEach(ListColumn::reset);
        indexOfFirstVisibleItem = 0;
        restoreFocusNecessary = true;
    }

    @Override
    public synchronized void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        // Get the list data
        var data = dataSource.get();

        if (data.isEmpty()) {
            this.renderEmptyList(matrices);
            return;
        }

        renderBackground(data, matrices);
        renderCellContents(data, matrices, mouseX, mouseY, delta);
        renderScrollbar(data, matrices);
        renderTooltips(matrices, mouseX, mouseY);

        if (this.restoreFocusNecessary) {
            this.restoreFocusNecessary = false;
            restoreFocusIfPossible();
        }
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
    public synchronized boolean mouseClicked(double mouseX, double mouseY, int button) {

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
        var colIndex = 0;
        for (var column : this.columnsDefinition.columns()) {

            for (var drawableEntry : column.getDrawableEntries()) {

                // Cast to element
                if (drawableEntry.getValue() instanceof Element element) {
                    if (element.mouseClicked(mouseX, finalMouseY, button)) {
                        parent.setFocused(element);
                        this.lastFocusedColumnIndex = colIndex;
                        this.lastFocusedElementId = drawableEntry.getKey();
                        result = true;
                    }
                }
            }

            colIndex++;
        }

        if (!result) {
            this.lastFocusedColumnIndex = null;
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

    public synchronized void tick() {
        this.columnsDefinition.columns().stream()
                .flatMap(ListColumn::getDrawables)
                .filter(drawable -> drawable instanceof TextFieldWidget)
                .map(drawable -> (TextFieldWidget) drawable)
                .forEach(TextFieldWidget::tick);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {

        return mouseY >= y && mouseY <= y + height && mouseX >= x && mouseX <= x + width;
    }

    private void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {

        // If hovered widget is not a tooltip holder
        if (!(hoveredCell instanceof ITooltipHolding tooltipHolder)) {
            return;
        }

        // If parent is not a screen
        if (!(parent instanceof Screen screenParent)) {
            return;
        }

        // Get the tooltip from the tooltip holder
        var tooltipText = tooltipHolder.getTooltip();

        // If the tooltip holder held an empty tooltip
        if (tooltipText == null) {
            return;
        }

        // Wrap lines
        var lines = textRenderer.wrapLines(tooltipText, 150);

        // Render the tooltip
        screenParent.renderOrderedTooltip(matrices, lines, mouseX, mouseY);
    }

    private void renderCellContents(List<T> data, MatrixStack matrices, int mouseX, int mouseY, float delta) {

        matrices.push();

        var listYOffset = this.indexOfFirstVisibleItem * this.rowHeight;
        mouseY += listYOffset;
        matrices.translate(0, -listYOffset, 0);

        // Sum the ratios of each column
        var ratioSum = columnsDefinition.columns().stream().map(ListColumn::getRatio).reduce(Integer::sum).get();

        var scrollbarOffset = data.size() <= this.numberOfVisibleItems ? 0 : (SCROLLBAR_WIDTH + SCROLLBAR_PADDING);

        var hoveredCellFound = false;

        synchronized (this) {
            int startX = this.x;
            for (var columnDefinition : columnsDefinition.columns()) {

                // Calculate the column width
                var columnWidth = (int) ((this.width - scrollbarOffset) * ((float) columnDefinition.getRatio() / (float) ratioSum));

                int index = 0;
                for (var dataItem : data) {

                    var cellStartY = this.y + (index * rowHeight);

                    // Build the list cell
                    var listCell = new ListCell<T>(startX, cellStartY, columnWidth, rowHeight, dataItem);

                    // Get the cell drawable
                    var cellDrawable = columnDefinition.getCellTemplate(listCell);

                    if (index >= this.indexOfFirstVisibleItem && index < this.indexOfFirstVisibleItem + this.numberOfVisibleItems) {

                        // Draw cell template
                        cellDrawable.render(matrices, mouseX, mouseY, delta);
                    }

                    // Check if this cell is hovered
                    if (mouseY >= cellStartY &&
                            mouseY <= cellStartY + rowHeight &&
                            mouseX >= startX &&
                            mouseX <= startX + columnWidth &&
                            isMouseOver(mouseX, mouseY - listYOffset)) {
                        hoveredCell = cellDrawable;
                        hoveredCellFound = true;
                    }

                    ++index;
                }

                startX += columnWidth;
            }
        }

        if (!hoveredCellFound) {
            hoveredCell = null;
        }

        matrices.pop();
    }

    private void renderBackground(List<?> data, MatrixStack matrices) {

        matrices.push();

        var listYOffset = this.indexOfFirstVisibleItem * this.rowHeight;
        matrices.translate(0, -listYOffset, 0);

        var scrollbarOffset = data.size() <= this.numberOfVisibleItems ? 0 : (SCROLLBAR_WIDTH + SCROLLBAR_PADDING);

        int index = 0;
        for (var ignored : data) {

            var cellStartY = this.y + (index * rowHeight);

            if (index >= this.indexOfFirstVisibleItem && index < this.indexOfFirstVisibleItem + this.numberOfVisibleItems) {

                // Draw background
                DrawableHelper.fill(matrices, this.x, cellStartY, this.x + this.width - scrollbarOffset, cellStartY + rowHeight, index % 2 == 0 ? ROW_COLOR : ALT_ROW_COLOR);
            }

            ++index;
        }

        matrices.pop();
    }

    private void renderScrollbar(List<?> data, MatrixStack matrices) {

        if (data.size() <= this.numberOfVisibleItems) {
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
        if (lastFocusedColumnIndex != null) {

            // Restore focus
            var element = (Element) columnsDefinition.columns().get(lastFocusedColumnIndex).getCellTemplate(lastFocusedElementId);

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
