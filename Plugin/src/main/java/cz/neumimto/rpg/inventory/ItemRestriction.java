package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 17.1.2016.
 */
public interface ItemRestriction {

    boolean canUse(IActiveCharacter character);
}
