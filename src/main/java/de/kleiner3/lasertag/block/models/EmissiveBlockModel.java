package de.kleiner3.lasertag.block.models;

import com.mojang.datafixers.util.Pair;
import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Base class for all simple emissive block models
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public abstract class EmissiveBlockModel extends AbstractEmissiveBlockModel {

    private final SpriteIdentifier textureSpriteId;
    private final SpriteIdentifier glowTextureSpriteId;

    private Sprite textureSprite;
    private Sprite glowTextureSprite;

    public EmissiveBlockModel(String texturePath, String glowTexturePath) {

        textureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, texturePath));
        glowTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowTexturePath));
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        // The textures this model (and all its model dependencies, and their dependencies, etc...!) depends on.

        var textureDependencies = new ArrayList<SpriteIdentifier>(2);

        textureDependencies.add(textureSpriteId);
        textureDependencies.add(glowTextureSpriteId);

        return textureDependencies;
    }

    @Override
    protected void getSprites(Function<SpriteIdentifier, Sprite> textureGetter) {

        textureSprite = textureGetter.apply(textureSpriteId);
        glowTextureSprite = textureGetter.apply(glowTextureSpriteId);
    }

    @Override
    public Sprite getParticleSprite() {
        // Block break particle
        return textureSprite;
    }

    @Override
    protected void addTextureToUnbakedModel(QuadEmitter emitter) {

        for(Direction direction : Direction.values()) {

            addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        }
    }
}
