package cz.neumimto.rpg.nms;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public abstract class NMSHandler {

    public abstract String getVersion();

    public abstract EntityDamageEvent handleEntityDamageEvent(LivingEntity target,
                                                              LivingEntity damager,
                                                              EntityDamageEvent.DamageCause source,
                                                              double damage,
                                                              double knockbackPower);

    public abstract void spawnFireworkExplosion(Location location, FireworkEffect effect, List<Player> observers);
}
