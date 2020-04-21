package cz.neumimto.rpg.api.damage;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;

public interface DamageService<W extends IActiveCharacter, T> {
    double getCharacterItemDamage(IActiveCharacter character, RpgItemType type);

    void recalculateCharacterWeaponDamage(IActiveCharacter character);

    void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemStack mainHand);

    void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemType type);

    void damageEntity(IEntity<T> character, double value);

    void init();

    boolean canDamage(W caster, T l);
}
