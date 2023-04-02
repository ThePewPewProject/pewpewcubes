package de.kleiner3.lasertag.command;

import net.minecraft.text.Text;

/**
 * Record holding the feedback information of a command
 * @param text The feedback text
 * @param overlay Boolean if the text should be overlayed in the HUD (true) or printed in chat (false)
 * @author Étienne Muser
 */
public record CommandFeedback(Text text, boolean overlay, boolean broadcast) {
}
