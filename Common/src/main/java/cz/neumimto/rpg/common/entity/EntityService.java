package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.common.IRpgElement;

import java.util.UUID;


public interface EntityService<T> {

    void remove(UUID uuid);

    void reload();

    IEntity get(T entity);

    double getEntityProperty(IEffectConsumer entity, int id);

    void remove(T entity);

    double getMobDamage(String dimension, String type);

    boolean handleMobDamage(String dimension, UUID uuid);

    double getExperiences(String dimension, String type, UUID uuid);

    double healEntity(IEntity entity, double amount, IRpgElement source);

    void updateWalkSpeed(IEntity<? extends T> entity);

}
