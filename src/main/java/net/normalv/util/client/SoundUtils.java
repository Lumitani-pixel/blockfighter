package net.normalv.util.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.normalv.BlockFighter;
import net.normalv.util.Util;

import static net.normalv.BlockFighter.MOD_ID;

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
        Identifier identifier = Identifier.fromNamespaceAndPath(MOD_ID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier,
                SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void playSound(SoundEvent sound) {
        playSound(sound, 1, 1);
    }

    public static void playSound(SoundEvent sound, float volume) {
        playSound(sound, volume, 1);
    }

    public static void playSound(SoundEvent sound, float volume, float pitch) {
        mc.player.playSound(sound, volume, pitch);
    }
}
