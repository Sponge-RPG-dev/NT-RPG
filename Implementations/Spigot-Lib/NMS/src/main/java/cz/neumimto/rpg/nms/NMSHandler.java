package cz.neumimto.rpg.nms;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.List;

public class NMSHandler {

    public NMSHandler() {}

    public List<String> getVersion() {
        return Collections.emptyList();
    }

    public EntityDamageEvent handleEntityDamageEvent(LivingEntity target,
                                                              LivingEntity damager,
                                                              EntityDamageEvent.DamageCause source,
                                                              double damage,
                                                              double knockbackPower) {

        return new EntityDamageEvent(damager, EntityDamageEvent.DamageCause.CONTACT, damage);
    }

    public void spawnFireworkExplosion(Location location, FireworkEffect effect, List<Player> observers) {

    }
}
