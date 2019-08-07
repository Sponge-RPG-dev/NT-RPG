package cz.neumimto.rpg.sponge.gui;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class GuiConfig {

    @Setting("SkillIcons")
    private Map<String, ItemType> skillIcons = new HashMap<>();

    public Map<String, ItemType> getSkillIcons() {
        return skillIcons;
    }
}
