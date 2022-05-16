package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.resources.Resource;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;

import java.util.UUID;

public class Health extends Resource {

    UUID uuid;

    public Health(UUID entity) {
        super("health");
        uuid = entity;
    }

    @Override
    protected void setMaxValue(double maxValue) {
        if (maxValue == 0) maxValue = 1;
        Bukkit.getServer().getPlayer(uuid).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxValue);
    }

    @Override
    public double getValue() {
        return Bukkit.getServer().getPlayer(uuid).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }
}
