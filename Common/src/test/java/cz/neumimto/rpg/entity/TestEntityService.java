package cz.neumimto.rpg.entity;

import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.entity.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

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
    public double getEntityProperty(IEffectConsumer entity, int id) {
        return Math.min(entity.getProperty(id), propertyService.getMaxPropertyValue(id));
    }

    @Override
    public double getExperiences(String dimension, String type, UUID uuid) {
        return 0;
    }

    @Override
    public double healEntity(IEntity entity, double amount, IRpgElement source) {
        return 0;
    }

    @Override
    public void updateWalkSpeed(IEntity<? extends TestCharacter> activeCharacter) {

    }

    @Override
    public void remove(UUID uuid) {

    }

    @Override
    public void reload() {

    }
}
