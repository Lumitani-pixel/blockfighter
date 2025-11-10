package net.normalv.systems.tools.client;

import net.normalv.event.events.impl.AttackEntityEvent;
import net.normalv.event.system.Subscribe;
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

    @Subscribe
    public void onAttackEntity(AttackEntityEvent event) {
        if(customHitSound.getValue()) {
            SoundUtils.playSound(SoundUtils.SKEET_HIT);
        }
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
}
