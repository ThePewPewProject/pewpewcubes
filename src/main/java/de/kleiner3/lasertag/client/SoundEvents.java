package de.kleiner3.lasertag.client;

import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Class for registering all sound events
 *
 * @author Étienne Muser
 */
public class SoundEvents {

    public static final Identifier PROCEDURAL_ARENA_INTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "procedural_arena_intro");
    public static final SoundEvent PROCEDURAL_ARENA_INTRO_MUSIC_SOUND_EVENT = new SoundEvent(PROCEDURAL_ARENA_INTRO_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier PROCEDURAL_ARENA_MUSIC_ID = new Identifier(LasertagMod.ID, "procedural_arena_music");
    public static final SoundEvent PROCEDURAL_ARENA_MUSIC_SOUND_EVENT = new SoundEvent(PROCEDURAL_ARENA_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier PROCEDURAL_ARENA_OUTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "procedural_arena_outro");
    public static final SoundEvent PROCEDURAL_ARENA_OUTRO_MUSIC_SOUND_EVENT = new SoundEvent(PROCEDURAL_ARENA_OUTRO_MUSIC_ID, Float.MAX_VALUE);

    public static final Identifier DESERT_ARENA_INTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "desert_arena_intro");
    public static final SoundEvent DESERT_ARENA_INTRO_MUSIC_SOUND_EVENT = new SoundEvent(DESERT_ARENA_INTRO_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier DESERT_ARENA_MUSIC_ID = new Identifier(LasertagMod.ID, "desert_arena_music");
    public static final SoundEvent DESERT_ARENA_MUSIC_SOUND_EVENT = new SoundEvent(DESERT_ARENA_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier DESERT_ARENA_OUTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "desert_arena_outro");
    public static final SoundEvent DESERT_ARENA_OUTRO_MUSIC_SOUND_EVENT = new SoundEvent(DESERT_ARENA_OUTRO_MUSIC_ID, Float.MAX_VALUE);

    public static final Identifier JUNGLE_ARENA_INTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "jungle_arena_intro");
    public static final SoundEvent JUNGLE_ARENA_INTRO_MUSIC_SOUND_EVENT = new SoundEvent(JUNGLE_ARENA_INTRO_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier JUNGLE_ARENA_MUSIC_ID = new Identifier(LasertagMod.ID, "jungle_arena_music");
    public static final SoundEvent JUNGLE_ARENA_MUSIC_SOUND_EVENT = new SoundEvent(JUNGLE_ARENA_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier JUNGLE_ARENA_OUTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "jungle_arena_outro");
    public static final SoundEvent JUNGLE_ARENA_OUTRO_MUSIC_SOUND_EVENT = new SoundEvent(JUNGLE_ARENA_OUTRO_MUSIC_ID, Float.MAX_VALUE);

    public static final Identifier FLOWER_FOREST_ARENA_INTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "flower_forest_arena_intro");
    public static final SoundEvent FLOWER_FOREST_ARENA_INTRO_MUSIC_SOUND_EVENT = new SoundEvent(FLOWER_FOREST_ARENA_INTRO_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier FLOWER_FOREST_ARENA_MUSIC_ID = new Identifier(LasertagMod.ID, "flower_forest_arena_music");
    public static final SoundEvent FLOWER_FOREST_ARENA_MUSIC_SOUND_EVENT = new SoundEvent(FLOWER_FOREST_ARENA_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier FLOWER_FOREST_ARENA_OUTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "flower_forest_arena_outro");
    public static final SoundEvent FLOWER_FOREST_ARENA_OUTRO_MUSIC_SOUND_EVENT = new SoundEvent(FLOWER_FOREST_ARENA_OUTRO_MUSIC_ID, Float.MAX_VALUE);

    public static final Identifier MEDIEVAL_CITY_ARENA_INTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "medieval_city_arena_intro");
    public static final SoundEvent MEDIEVAL_CITY_ARENA_INTRO_MUSIC_SOUND_EVENT = new SoundEvent(MEDIEVAL_CITY_ARENA_INTRO_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier MEDIEVAL_CITY_ARENA_MUSIC_ID = new Identifier(LasertagMod.ID, "medieval_city_arena_music");
    public static final SoundEvent MEDIEVAL_CITY_ARENA_MUSIC_SOUND_EVENT = new SoundEvent(MEDIEVAL_CITY_ARENA_MUSIC_ID, Float.MAX_VALUE);
    public static final Identifier MEDIEVAL_CITY_ARENA_OUTRO_MUSIC_ID = new Identifier(LasertagMod.ID, "medieval_city_arena_outro");
    public static final SoundEvent MEDIEVAL_CITY_ARENA_OUTRO_MUSIC_SOUND_EVENT = new SoundEvent(MEDIEVAL_CITY_ARENA_OUTRO_MUSIC_ID, Float.MAX_VALUE);

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, PROCEDURAL_ARENA_OUTRO_MUSIC_ID, PROCEDURAL_ARENA_OUTRO_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, PROCEDURAL_ARENA_MUSIC_ID, PROCEDURAL_ARENA_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, PROCEDURAL_ARENA_INTRO_MUSIC_ID, PROCEDURAL_ARENA_INTRO_MUSIC_SOUND_EVENT);

        Registry.register(Registry.SOUND_EVENT, JUNGLE_ARENA_OUTRO_MUSIC_ID, JUNGLE_ARENA_OUTRO_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, JUNGLE_ARENA_MUSIC_ID, JUNGLE_ARENA_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, JUNGLE_ARENA_INTRO_MUSIC_ID, JUNGLE_ARENA_INTRO_MUSIC_SOUND_EVENT);

        Registry.register(Registry.SOUND_EVENT, DESERT_ARENA_OUTRO_MUSIC_ID, DESERT_ARENA_OUTRO_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, DESERT_ARENA_MUSIC_ID, DESERT_ARENA_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, DESERT_ARENA_INTRO_MUSIC_ID, DESERT_ARENA_INTRO_MUSIC_SOUND_EVENT);

        Registry.register(Registry.SOUND_EVENT, FLOWER_FOREST_ARENA_OUTRO_MUSIC_ID, FLOWER_FOREST_ARENA_OUTRO_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, FLOWER_FOREST_ARENA_MUSIC_ID, FLOWER_FOREST_ARENA_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, FLOWER_FOREST_ARENA_INTRO_MUSIC_ID, FLOWER_FOREST_ARENA_INTRO_MUSIC_SOUND_EVENT);

        Registry.register(Registry.SOUND_EVENT, MEDIEVAL_CITY_ARENA_OUTRO_MUSIC_ID, MEDIEVAL_CITY_ARENA_OUTRO_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, MEDIEVAL_CITY_ARENA_MUSIC_ID, MEDIEVAL_CITY_ARENA_MUSIC_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, MEDIEVAL_CITY_ARENA_INTRO_MUSIC_ID, MEDIEVAL_CITY_ARENA_INTRO_MUSIC_SOUND_EVENT);
    }
}
