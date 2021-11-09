package cz.neumimto.rpg.common.events.damage;

import cz.neumimto.rpg.common.items.RpgItemStack;

import java.util.Optional;

public interface IEntityWeaponDamageLateEvent extends DamageIEntityLateEvent {

    Optional<RpgItemStack> getWeapon();

    void setWeapon(RpgItemStack weapon);

}
