package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.api.events.entity.TargetIEntityEvent;

public interface DamageIEntityLateEvent extends TargetIEntityEvent {

    double getDamage();

    void setDamage(double damage);

}
