package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.items.RpgItemType;

import java.util.Set;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public interface CharacterWeaponUpdateEvent extends TargetCharacterEvent {

    Set<RpgItemType> getAllowedWeapons();

    void setWeapons(Set<RpgItemType> weapons);

}
