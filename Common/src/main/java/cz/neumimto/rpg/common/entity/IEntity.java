package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IEntity<T> extends IEffectConsumer {

    IEntityType getType();

    UUID getUUID();

    IEntityResource getHealth();

    boolean isFriendlyTo(IActiveCharacter character);

    T getEntity();
}
