package cz.neumimto.rpg.entities;

import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IEntity extends IEffectConsumer {

    IEntityType getType();

    IEntityResource getHealth();

    boolean isFriendlyTo(IActiveCharacter character);
}
