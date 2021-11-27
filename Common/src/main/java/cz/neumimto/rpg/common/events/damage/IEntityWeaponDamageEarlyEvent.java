package cz.neumimto.rpg.common.events.damage;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.items.RpgItemStack;

import java.util.Optional;

public interface IEntityWeaponDamageEarlyEvent extends DamageIEntityEarlyEvent {

    Optional<RpgItemStack> getWeapon();

    void setWeapon(RpgItemStack weapon);

    void setDamager(IEntity attacker);

    IEntity getDamager();
}
