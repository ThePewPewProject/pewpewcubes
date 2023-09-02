package de.kleiner3.lasertag.block.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Base class for all simple emissive block models
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public abstract class EmissiveBlockModel extends AbstractEmissiveBlockModel {


    public EmissiveBlockModel(String texturePath, String glowTexturePath) {
        super(texturePath, glowTexturePath);
    }

    @Override
    protected void addTextureToUnbakedModel(QuadEmitter emitter, Sprite textureSprite) {

        for(Direction direction : Direction.values()) {

            addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        }
    }
}
