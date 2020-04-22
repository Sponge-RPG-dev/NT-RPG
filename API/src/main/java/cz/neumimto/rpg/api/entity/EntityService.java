package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.IRpgElement;

import java.util.UUID;


public interface EntityService<T> {

    void remove(UUID uuid);

    void reload();

    IEntity get(T entity);

    float getEntityProperty(IEffectConsumer entity, int id);

    void remove(T entity);

    double getMobDamage(String dimension, String type);

    boolean handleMobDamage(UUID uuid);

    double getExperiences(String dimension, String type, UUID uuid);

    double healEntity(IEntity entity, float amount, IRpgElement source);

    void updateWalkSpeed(IEntity<? extends T> entity);

}
