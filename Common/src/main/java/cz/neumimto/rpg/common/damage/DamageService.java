package cz.neumimto.rpg.common.damage;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.items.RpgItemType;

public interface DamageService<W extends IActiveCharacter, T, E extends IEntity<T>> {
    double getCharacterItemDamage(IActiveCharacter character, RpgItemType type);

    void recalculateCharacterWeaponDamage(IActiveCharacter character);

    void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemStack mainHand);

    void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemType type);

    void damageEntity(E entity, double value);

    void init();

    boolean canDamage(W caster, T l);
}
