package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public interface CharacterWeaponUpdateEvent extends TargetCharacterEvent {

    Map<SpongeRpgItemType, Double> getAllowedWeapons();

    void setWeapons(Map<SpongeRpgItemType, Double> weapons);
}
