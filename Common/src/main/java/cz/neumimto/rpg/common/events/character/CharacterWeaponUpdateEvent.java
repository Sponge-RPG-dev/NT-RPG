package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.items.RpgItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public interface CharacterWeaponUpdateEvent extends TargetCharacterEvent {

    Map<RpgItemType, Double> getAllowedWeapons();

    void setWeapons(Map<RpgItemType, Double> weapons);

}
