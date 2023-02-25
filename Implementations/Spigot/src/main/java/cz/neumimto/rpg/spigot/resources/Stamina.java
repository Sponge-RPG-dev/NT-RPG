package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceDefinition;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Stamina extends Resource {

    UUID uuid;

    public Stamina(UUID entity, ResourceDefinition resourceDefinition) {
        super(resourceDefinition);
        uuid = entity;
    }

    @Override
    protected void setMaxValue(double maxValue) {
        if (maxValue == 0) maxValue = 1;
        //Bukkit.getServer().getPlayer(uuid).getFoodLevel();
    }

    @Override
    public double getValue() {
        return Bukkit.getServer().getPlayer(uuid).getFoodLevel();
    }
}
