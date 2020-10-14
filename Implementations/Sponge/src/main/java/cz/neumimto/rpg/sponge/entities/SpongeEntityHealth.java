package cz.neumimto.rpg.sponge.entities;

import cz.neumimto.rpg.api.entity.IEntityResource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 18.6.2017.
 */
public class SpongeEntityHealth implements IEntityResource {

    public Living entity;

    public SpongeEntityHealth(Living entity) {
        this.entity = entity;
    }

    @Override
    public double getMaxValue() {
        return entity.maxHealth().getMaxValue();
    }

    @Override
    public void setMaxValue(double f) {
        entity.offer(Keys.MAX_HEALTH, f);
    }

    @Override
    public double getValue() {
        return entity.get(Keys.HEALTH).get();
    }

    @Override
    public void setValue(double f) {
        entity.offer(Keys.HEALTH, f);
    }

    @Override
    public double getRegen() {
        return 0;
    }

    @Override
    public void setRegen(float f) {

    }
}
