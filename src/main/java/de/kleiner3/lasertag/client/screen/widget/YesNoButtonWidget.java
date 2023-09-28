package de.kleiner3.lasertag.client.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

/**
 * Simple yes / no boolean button widget
 *
 * @author Étienne Muser
 */
public class YesNoButtonWidget extends PressableWidget {

    private boolean value;
    private UpdateCallback callback;

    public YesNoButtonWidget(int x, int y, int width, int height, boolean initialValue, UpdateCallback callback) {
        super(x, y, width, height, Text.empty());
        this.value = initialValue;
        this.callback = callback;
        this.updateText();
    }

    @Override
    public void onPress() {
        this.value = !this.value;
        this.callback.onValueChange(this.value);

        this.updateText();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Environment(EnvType.CLIENT)
    public interface UpdateCallback {
        void onValueChange(boolean value);
    }

    private void updateText() {
        this.setMessage(this.value ? Text.translatable("gui.yes") : Text.translatable("gui.no"));
    }
}
