package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.common.entity.IEntityResource;
import org.bukkit.entity.LivingEntity;

public class SpigotEntityHealth implements IEntityResource {

    private LivingEntity entity;

    public SpigotEntityHealth(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public double getMaxValue() {
        return entity.getMaxHealth();
    }

    @Override
    public void setMaxValue(double f) {
        entity.setMaxHealth(f);
    }

    @Override
    public double getValue() {
        return entity.getHealth();
    }

    @Override
    public void setValue(double f) {
        entity.setHealth(f);
    }

    @Override
    public double getRegen() {
        return 0;
    }

    @Override
    public void setRegen(float f) {

    }
}
