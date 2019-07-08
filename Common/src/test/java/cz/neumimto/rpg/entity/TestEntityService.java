package cz.neumimto.rpg.entity;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.common.entity.TestCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TestEntityService implements EntityService<TestCharacter> {

    @Inject
    private PropertyService propertyService;

    @Override
    public IEntity get(TestCharacter entity) {
        return entity;
    }

    @Override
    public void remove(TestCharacter entity) {

    }

    @Override
    public float getEntityProperty(IEffectConsumer entity, int id) {
        return Math.min(entity.getProperty(id), propertyService.getMaxPropertyValue(id));
    }

    @Override
    public double getMobDamage(String dimension, String type) {
        return 0;
    }

    @Override
    public double getExperiences(String dimension, String type) {
        return 0;
    }

    @Override
    public double healEntity(IEntity entity, float amount, IRpgElement source) {
        return 0;
    }

    @Override
    public void updateWalkSpeed(IEntity<? extends TestCharacter> activeCharacter) {

    }

    @Override
    public void reload() {

    }
}
