package net.normalv.util.client;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.normalv.BlockFighter;
import net.normalv.util.interfaces.Util;

public class SoundUtils implements Util {
    private SoundUtils() {
    }

    public static void initialize() {
        BlockFighter.LOGGER.info("Registering " + BlockFighter.MOD_NAME + " Sounds");
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
        if (mc.player != null && mc.world != null)
            mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.BLOCKS, 1f, 1f);
    }
}
