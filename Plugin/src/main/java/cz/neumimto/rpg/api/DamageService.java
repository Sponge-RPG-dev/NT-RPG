package cz.neumimto.rpg.api;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.players.IActiveCharacter;

public interface DamageService {
    double getCharacterItemDamage(IActiveCharacter character, RpgItemType type);

    void recalculateCharacterWeaponDamage(IActiveCharacter character);

    void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemStack mainHand);

    void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemType type);
}
