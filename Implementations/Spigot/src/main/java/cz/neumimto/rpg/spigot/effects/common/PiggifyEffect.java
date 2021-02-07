package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PiggifyEffect extends EffectBase {

    public static Set<UUID> entities = new HashSet<>();

    public static final String name = "Piggify";

    private LivingEntity le;


    public PiggifyEffect(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
        le = (LivingEntity) consumer.getEntity();
    }


    @Override
    public void onApply(IEffect self) {
        Location location = le.getLocation();
        EntityType e = le.isInWater() ? EntityType.TURTLE : EntityType.PIG;
        Entity entity = location.getWorld().spawnEntity(location, e);
        entity.addPassenger(le);
        entities.add(entity.getUniqueId());
    }

    @Override
    public void onRemove(IEffect self) {
        Entity vehicle = le.getVehicle();
        if (vehicle != null) {
            UUID uniqueId = vehicle.getUniqueId();
            vehicle.remove();
            entities.remove(uniqueId);
        }
    }
}
