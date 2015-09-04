package cz.neumimto.events;

import cz.neumimto.Weapon;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 12.2.2015.
 */
public class WeaponEquipEvent extends CancellableEvent {
    final IActiveCharacter player;
    final Weapon newItem;
    final Weapon lastItem;

    public WeaponEquipEvent(IActiveCharacter player, Weapon newItem, Weapon currentWeapon) {
        this.player = player;
        this.newItem = newItem;
        this.lastItem = currentWeapon;
    }

    public IActiveCharacter getPlayer() {
        return player;
    }

    public Weapon getNewItem() {
        return newItem;
    }

    public Weapon getLastItem() {
        return lastItem;
    }

}
