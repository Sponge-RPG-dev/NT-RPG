package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.items.SpongeRpgItemType;
import cz.neumimto.rpg.players.IActiveCharacter;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class CharacterWeaponUpdateEvent extends AbstractCharacterEvent {

    private final Map<SpongeRpgItemType, Double> weapons;

    public CharacterWeaponUpdateEvent(IActiveCharacter character, Map<SpongeRpgItemType, Double> weapons) {
        super(character);
        this.weapons = weapons;
    }

    public Map<SpongeRpgItemType, Double> getAllowedWeapons() {
        return weapons;
    }
}
