package de.kleiner3.lasertag.worldgen.chunkgen.template;

import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ProceduralArenaType;

/**
 * @author Étienne Muser
 */
public class TemplateRegistry {

    public static ArenaTemplate getTemplate(ArenaType arenaType, ProceduralArenaType proceduralArenaType, long seed) {

        if (arenaType == ArenaType.PROCEDURAL) {
            return new ProceduralArenaTemplate(proceduralArenaType, seed);
        } else {
            return new PrebuildArenaTemplate(arenaType);
        }
    }
}
