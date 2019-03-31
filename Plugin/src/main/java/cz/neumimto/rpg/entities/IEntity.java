package cz.neumimto.rpg.entities;

import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IEntity<T extends Living> extends IEffectConsumer<T> {

	IEntityType getType();

	IEntityResource getHealth();

	boolean isFriendlyTo(IActiveCharacter character);
}
