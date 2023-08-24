package de.kleiner3.lasertag.block.models;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BlockModelProvider implements ModelResourceProvider {
    public static final Identifier ARENA_BLOCK_MODEL = new Identifier(LasertagMod.ID, "block/arena_block_model");

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if (resourceId.equals(ARENA_BLOCK_MODEL)) {
            return new ArenaBlockModel();
        }

        return null;
    }
}
