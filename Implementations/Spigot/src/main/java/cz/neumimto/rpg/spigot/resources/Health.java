package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;

import java.util.UUID;

public class Health extends Resource {

    private UUID uuid;

    public Health(IActiveCharacter character) {
        super("health");
        uuid = character.getUUID();
    }

    @Override
    protected void setMaxValue(double maxValue) {
        Bukkit.getServer().getPlayer(uuid).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxValue);
    }

    @Override
    public double getValue() {
        return Bukkit.getServer().getPlayer(uuid).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }
}
