package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.command.suggestions.MapSuggestionProvider;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ProceduralArenaType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Command to reload the arena in game
 *
 * @author Étienne Muser
 */
public class LoadMapCommand extends ServerFeedbackCommand {
    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        var mapTranslatableName = StringArgumentType.getString(context, "map");
        var server = context.getSource().getServer();

        var arenaTypeOptional = Arrays.stream(ArenaType.values())
                .filter(m -> m.translatableName.equals(mapTranslatableName))
                .findFirst();

        var proceduralTypeOptional = Arrays.stream(ProceduralArenaType.values())
                .filter(m -> m.translatableName.equals(mapTranslatableName))
                .findFirst();

        // If is procedural map
        if (proceduralTypeOptional.isPresent()) {
            arenaTypeOptional = Optional.of(ArenaType.PROCEDURAL);
        }

        if (arenaTypeOptional.isEmpty()) {

            return Optional.of(new CommandFeedback(Text.literal("Could not find map.").formatted(Formatting.RED), false, false));
        }

        try {
            server.getLasertagServerManager().getMapManager().loadMap(arenaTypeOptional.get(), proceduralTypeOptional.orElse(ProceduralArenaType.SMALL_2V2));
        } catch (Exception e) {
            return Optional.of(new CommandFeedback(Text.literal("Unexpected error while loading map: " + e.getMessage()).formatted(Formatting.RED), false, false));
        }

        return Optional.of(new CommandFeedback(Text.literal("Map loaded."), false, false));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("loadMap")
                .requires(s -> s.hasPermissionLevel(4))
                .then(argument("map", word())
                        .suggests(MapSuggestionProvider.getInstance())
                        .executes(new LoadMapCommand())));
    }
}
