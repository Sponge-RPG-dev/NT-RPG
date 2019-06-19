package cz.neumimto.rpg.api.events.damage;

import cz.neumimto.rpg.api.events.Cancellable;
import cz.neumimto.rpg.api.events.entity.TargetIEntityEvent;

public interface DamageIEntityEarlyEvent extends TargetIEntityEvent, Cancellable {

    double getDamage();

    void setDamage(double damage);

}
