package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.IRpgElement;


public interface EntityService<T> {

    IEntity get(T entity);

    void remove(T entity);

    double getMobDamage(String dimension, String type);

    double getExperiences(String dimension, String type);

    float getEntityProperty(IEffectConsumer entity, int id);

    double healEntity(IEntity entity, float amount, IRpgElement source);

    void updateWalkSpeed(IEntity<? extends T> activeCharacter);

    void reload();
}
