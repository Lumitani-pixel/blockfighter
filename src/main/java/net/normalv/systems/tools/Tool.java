package net.normalv.systems.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.normalv.BlockFighter;
import net.normalv.systems.tools.setting.Setting;
import net.normalv.systems.tools.setting.SettingFactory;
import net.normalv.util.interfaces.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tool implements Util, SettingFactory {
    private String name;
    private String description;
    private String displayName;
    private Category category;
    private boolean isEnabled;
    private List<Setting<?>> settings = new ArrayList<>();

    public Tool(String name, String description, Category category){
        this.name = name;
        this.description = description;
        this.displayName = name;
        this.category = category;

        registerSettings();
        isEnabled = false;
    }

    public void registerSettings() {
    }

    public void onToggle() {
    }

    public void onEnabled() {
    }

    public void onDisabled() {
    }

    public void onTick() {
    }

    public void onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
    }

    public void onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, HitResult hitResult) {
    }

    public void onSettingChange() {
    }

    public void addDisplayInfo(String... strings) {
        this.displayName = name + " : " + Arrays.asList(strings);
    }

    public void toggle() {
        if(!isEnabled) enable();
        else disable();
        onToggle();
    }

    public void enable() {
        BlockFighter.textManager.sendTextClientSide(BlockFighter.textManager.getToggleMsg(this, true));
        isEnabled = true;
        onEnabled();
    }

    public void disable() {
        BlockFighter.textManager.sendTextClientSide(BlockFighter.textManager.getToggleMsg(this, false));
        isEnabled = false;
        onDisabled();
    }

    public void info(String info) {
        BlockFighter.textManager.sendTextClientSide(BlockFighter.textManager.getInfoMsg(this, info));
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Category getCategory() {
        return category;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    @Override
    public <T extends Setting<?>> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    public enum Category{
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        CLIENT("Client"),
        MISC("Misc");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
