package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.IRpgElement;


public interface EntityService<T> {

    void reload();

    IEntity get(T entity);

    float getEntityProperty(IEffectConsumer entity, int id);

    void remove(T entity);

    double getMobDamage(String dimension, String type);

    double getExperiences(String dimension, String type);

    double healEntity(IEntity entity, float amount, IRpgElement source);

    void updateWalkSpeed(IEntity<? extends T> activeCharacter);

}
