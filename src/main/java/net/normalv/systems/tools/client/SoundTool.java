package net.normalv.systems.tools.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.normalv.systems.tools.Tool;
import net.normalv.systems.tools.setting.Setting;
import net.normalv.util.client.SoundUtils;

public class SoundTool extends Tool {
    Setting<Boolean> customHitSound;
    Setting<Boolean> customDeathSound;
    Setting<Boolean> customTotemSound;
    Setting<Boolean> customWalkSound;
    private int soundDelay = 12;
    private boolean playedDeathSound = false;

    public SoundTool() {
        super("Sounds", "Enable and disable sounds for certain events", Category.CLIENT);
    }

    @Override
    public void registerSettings() {
        customHitSound = bool("hitsound", true);
        customDeathSound = bool("deathsound", true);
        customTotemSound = bool("totemsound", true);
        customWalkSound = bool("walksound", true);
    }

    @Override
    public void onTick() {
        handleWalkSound();
        handleDeathSound();
    }

    private void handleWalkSound() {
        soundDelay--;
        if(!mc.player.isOnGround() || soundDelay>0 || !customWalkSound.getValue() || mc.player.getVelocity().lengthSquared()<0.01) return;
        SoundUtils.playSound(SoundUtils.SPONGE_WALK, jrandom.nextFloat(0.2f, 0.30001f), jrandom.nextFloat(0.9f, 1.0001f));
        soundDelay=12;
    }

    public void handleDeathSound() {
        if(mc.player.isAlive()) {
            playedDeathSound = false;
            return;
        }
        if(playedDeathSound) return;
        playedDeathSound = true;
        SoundUtils.playSound(SoundUtils.BOO_WOMP);
    }

    @Override
    public void onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, HitResult hitResult) {
        if(customHitSound.getValue()) {
            SoundUtils.playSound(SoundUtils.SKEET_HIT);
        }
    }
}
