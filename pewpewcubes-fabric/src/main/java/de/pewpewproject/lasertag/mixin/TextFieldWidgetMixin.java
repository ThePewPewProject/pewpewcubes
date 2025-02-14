package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.client.screen.widget.ITooltipHolding;
import de.pewpewproject.lasertag.client.screen.widget.ITooltipSetter;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the TextField.class to inject the ITooltipHolding and ITooltipSetter interfaces
 *
 * @author Étienne Muser
 */
@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin implements ITooltipHolding, ITooltipSetter {

    private Text tooltip;

    @Override
    public void setTooltip(Text tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Text getTooltip() {
        return tooltip;
    }
}
