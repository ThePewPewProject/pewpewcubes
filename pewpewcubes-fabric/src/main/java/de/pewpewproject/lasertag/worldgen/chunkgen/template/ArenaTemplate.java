package de.pewpewproject.lasertag.worldgen.chunkgen.template;

import net.minecraft.util.math.Vec3i;

/**
 * @author Étienne Muser
 */
public abstract class ArenaTemplate {

    protected final boolean isProcedural;

    ArenaTemplate(boolean isProcedural) {

        this.isProcedural = isProcedural;
    }

    public boolean isProcedural() {
        return this.isProcedural;
    }

    public abstract Vec3i getArenaSize();

    public abstract Vec3i getPlacementOffset();
}
