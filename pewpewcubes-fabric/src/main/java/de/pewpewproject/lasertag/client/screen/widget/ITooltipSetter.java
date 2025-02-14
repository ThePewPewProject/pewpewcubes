package de.pewpewproject.lasertag.client.screen.widget;

import net.minecraft.text.Text;

/**
 * Interface for an object capable of setting a tooltip on itself
 *
 * @author Étienne Muser
 */
public interface ITooltipSetter {
    default void setTooltip(Text tooltip) {}
}
