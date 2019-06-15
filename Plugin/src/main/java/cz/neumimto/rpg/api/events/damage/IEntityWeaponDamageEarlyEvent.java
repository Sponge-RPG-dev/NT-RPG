package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.api.events.Cancellable;
import cz.neumimto.rpg.api.items.RpgItemStack;

import java.util.Optional;


public interface IEntityWeaponDamageEarlyEvent extends DamageIEntityEarlyEvent, Cancellable {

    Optional<RpgItemStack> getWeapon();

    void setWeapon(RpgItemStack weapon);
}
