package cz.neumimto.rpg.sponge.gui;

import com.electronwill.nightconfig.core.conversion.Path;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.Map;

public class GuiConfig {

    @Path("icons")
    private Map<String, ItemType> skillIcons = new HashMap<>();

    public Map<String, ItemType> getSkillIcons() {
        return skillIcons;
    }
}
