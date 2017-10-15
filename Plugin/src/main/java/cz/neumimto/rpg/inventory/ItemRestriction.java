package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 17.1.2016.
 */
@FunctionalInterface
public interface ItemRestriction<T> {

	boolean canUse(IActiveCharacter character, T requiredValue);

}
