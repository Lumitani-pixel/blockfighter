package net.normalv.util.client;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.normalv.BlockFighter;
import net.normalv.util.Util;

public class SoundUtils implements Util {
    private SoundUtils() {
    }

    public static void initialize() {
        BlockFighter.LOGGER.info("Registering {} Sounds", BlockFighter.MOD_NAME);
    }

    public static final SoundEvent BOO_WOMP = registerSound("boowomp");
    public static final SoundEvent SKEET_HIT = registerSound("skeethit");
    public static final SoundEvent DEATH_BELL = registerSound("deathbell");
    public static final SoundEvent SPONGE_WALK = registerSound("spongewalk");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(BlockFighter.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void playSound(SoundEvent sound) {
        playSound(sound, 1, 1);
    }

    public static void playSound(SoundEvent sound, float volume) {
        playSound(sound, volume, 1);
    }

    public static void playSound(SoundEvent sound, float volume, float pitch) {
        mc.getSoundManager().play(PositionedSoundInstance.ui(sound, pitch, volume));
    }
}
