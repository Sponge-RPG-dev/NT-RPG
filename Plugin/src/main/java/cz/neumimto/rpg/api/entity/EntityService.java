package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.IRpgElement;
import org.spongepowered.api.entity.Entity;

import java.util.Collection;
import java.util.UUID;

public interface EntityService {

    IEntity get(UUID id);

    void remove(UUID e);

    void remove(Collection<Entity> l);

    double getMobDamage(String dimension, String type);

    double getExperiences(String dimension, String type);

    float getEntityProperty(IEffectConsumer entity, int id);

    double healEntity(IEntity entity, float amount, IRpgElement source);
}
