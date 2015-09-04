package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 13.3.2015.
 */
public class WeaponSwitchEvent extends CharacterEvent {

    public WeaponSwitchEvent(IActiveCharacter IActiveCharacter, ItemStack newWeapon) {
        super(IActiveCharacter);
    }
}
