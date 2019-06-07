package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.CharacterWeaponUpdateEvent;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class SpongeCharacterWeaponUpdateEvent extends AbstractCharacterEvent implements CharacterWeaponUpdateEvent {

    private Map<RpgItemType, Double> weapons;

    public Map<RpgItemType, Double> getAllowedWeapons() {
        return weapons;
    }

    public void setWeapons(Map<RpgItemType, Double> weapons) {
        this.weapons = weapons;
    }
}
