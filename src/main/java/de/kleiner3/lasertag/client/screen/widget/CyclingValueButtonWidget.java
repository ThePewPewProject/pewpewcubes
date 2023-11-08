package de.kleiner3.lasertag.client.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

/**
 * Widget for a button that cycles between a set of values.
 *
 * @author Étienne Muser
 */
public class CyclingValueButtonWidget<T> extends PressableWidget {

    private int currentIndex;
    private final List<T> values;
    private final UpdateCallback<T> callback;
    private final Function<T, Text> textGetter;

    public CyclingValueButtonWidget(int x, int y,
                                    int width, int height,
                                    T initialValue, Function<T, Text> textGetter,
                                    List<T> values,
                                    UpdateCallback<T> callback) {
        super(x, y, width, height, Text.empty());
        this.currentIndex = values.indexOf(initialValue);
        this.values = values;
        this.callback = callback;
        this.textGetter = textGetter;
        this.updateText();
    }

    @Override
    public void onPress() {
        this.currentIndex = (++currentIndex) % this.values.size();
        this.callback.onValueChange(this.values.get(this.currentIndex));

        this.updateText();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}

    @Environment(EnvType.CLIENT)
    public interface UpdateCallback<T> {
        void onValueChange(T value);
    }

    private void updateText() {
        this.setMessage(this.textGetter.apply(this.values.get(this.currentIndex)));
    }
}
