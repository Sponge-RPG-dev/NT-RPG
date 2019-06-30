package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.api.items.RpgItemStack;

import java.util.Optional;

public interface IEntityWeaponDamageLateEvent extends DamageIEntityLateEvent {

    Optional<RpgItemStack> getWeapon();

    void setWeapon(RpgItemStack weapon);

}
