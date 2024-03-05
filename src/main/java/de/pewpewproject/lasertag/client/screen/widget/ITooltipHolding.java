package de.pewpewproject.lasertag.client.screen.widget;

import net.minecraft.text.Text;

/**
 * Interface for an object capable of holding a tooltip text
 *
 * @author Étienne Muser
 */
public interface ITooltipHolding {
    default Text getTooltip() {
        return null;
    }
}
