package cz.neumimto.rpg.spigot.resources;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class MobHealth extends Health {

    public MobHealth(UUID entity) {
        super(entity);
    }

    @Override
    protected void setMaxValue(double maxValue) {
        ((LivingEntity)Bukkit.getServer().getEntity(uuid)).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxValue);
    }

    @Override
    public double getValue() {
        return ((LivingEntity)Bukkit.getServer().getEntity(uuid)).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }
}
