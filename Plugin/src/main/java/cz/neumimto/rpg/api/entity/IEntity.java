package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IEntity extends IEffectConsumer {

    IEntityType getType();

    IEntityResource getHealth();

    boolean isFriendlyTo(IActiveCharacter character);
}
