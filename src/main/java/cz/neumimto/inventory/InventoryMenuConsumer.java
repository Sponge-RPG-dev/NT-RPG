package cz.neumimto.inventory;

import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@FunctionalInterface
public interface InventoryMenuConsumer {

    boolean onClick(ClickType type, IActiveCharacter character);

    InventoryMenuConsumer EMPTY = (type, character) -> false;
}
