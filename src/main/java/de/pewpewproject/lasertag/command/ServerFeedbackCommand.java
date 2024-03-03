package de.pewpewproject.lasertag.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.LasertagMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

/**
 * Abstract base class for all ServerCommands.
 * Catches all exceptions and handles responses in chat.
 *
 * @author Étienne Muser
 */
public abstract class ServerFeedbackCommand implements Command<ServerCommandSource> {
    /**
     * Exectues the command and gets the response message.
     * @param context The command context
     * @return Optional of the command feedback. If this is present, then this text will be printed.
     */
    protected abstract Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context);

    @Override
    public int run(CommandContext<ServerCommandSource> context) {

        try {
            // Execute command and get response
            var response = execute(context);

            // If response is present
            response.ifPresent(feedback -> {
                // Get the command source
                var source = context.getSource();

                // If should broadcast
                if (feedback.broadcast()) {
                    // Send broadcast
                    source.getServer().getPlayerManager().broadcast(feedback.text(), feedback.overlay());
                } else {
                    // Send response to player
                    source.getPlayer().sendMessage(feedback.text(), feedback.overlay());
                }
            });
        } // Rethrow command syntax exception
        catch (Exception e) {
            // Log the error
            LasertagMod.LOGGER.error("An error occured while executing a '" + this.getClass().getName() + "':", e);

            // Get the command source
            var source = context.getSource();

            // Send error message in chat
            source.getPlayer().sendMessage(Text.literal("An error occurred while executing that command. See the log for details.").formatted(Formatting.RED));
        }

        return SINGLE_SUCCESS;
    }
}
