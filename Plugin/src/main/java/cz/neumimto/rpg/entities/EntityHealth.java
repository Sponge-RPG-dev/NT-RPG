package cz.neumimto.rpg.entities;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.players.IEntityHealth;
import org.spongepowered.api.data.key.Keys;

/**
 * Created by NeumimTo on 18.6.2017.
 */
public class EntityHealth implements IEntityHealth {

	public IEntity entity;

	public EntityHealth(IEntity entity) {
		this.entity = entity;
	}

	public IEntity getEntity() {
		return entity;
	}

	public void setEntity(IEntity entity) {
		this.entity = entity;
	}

	@Override
	public double getMaxValue() {
		return entity.getEntity().maxHealth().getMaxValue();
	}

	@Override
	public void setMaxValue(double f) {
		entity.getEntity().offer(Keys.MAX_HEALTH, f);
	}

	@Override
	public double getValue() {
		return entity.getEntity().get(Keys.HEALTH).get();
	}

	@Override
	public void setValue(double f) {
		entity.getEntity().offer(Keys.HEALTH, f);
	}

	@Override
	public double getRegen() {
		return 0;
	}

	@Override
	public void setRegen(float f) {

	}
}
