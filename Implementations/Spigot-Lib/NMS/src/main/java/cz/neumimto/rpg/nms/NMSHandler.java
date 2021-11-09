package cz.neumimto.rpg.nms;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class NMSHandler {

    public abstract String getVersion();

    public abstract EntityDamageEvent handleEntityDamageEvent(LivingEntity target,
                                                              LivingEntity damager,
                                                              EntityDamageEvent.DamageCause source,
                                                              double damage,
                                                              double knockbackPower);
}
