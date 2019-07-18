package cz.neumimto.rpg.sponge.entities;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ISpongeEntity<T extends Living> extends IEffectConsumer, IEntity<T> {

    T getEntity();

    UUID getUUID();

    default void addPotionEffect(PotionEffectType p, int amplifier, long duration) {
        PotionEffect build = PotionEffect.builder().potionType(p).amplifier(amplifier).duration((int) duration).build();
        List<PotionEffect> potionEffects = getEntity().get(Keys.POTION_EFFECTS).get();
        potionEffects.add(build);
        getEntity().offer(Keys.POTION_EFFECTS, potionEffects);
    }

    default void addPotionEffect(PotionEffectType p, int amplifier, long duration, boolean particles) {
        PotionEffect build = PotionEffect.builder().particles(particles).potionType(p).amplifier(amplifier).duration((int) duration).build();
        addPotionEffect(build);
    }

    default void removePotionEffect(PotionEffectType type) {
        List<PotionEffect> potionEffects = getEntity().get(Keys.POTION_EFFECTS).get();
        List<PotionEffect> l = potionEffects.stream().filter(p -> p.getType() != type).collect(Collectors.toList());
        getEntity().offer(Keys.POTION_EFFECTS, l);
    }

    default boolean hasPotionEffect(PotionEffectType type) {
        List<PotionEffect> potionEffects = getEntity().get(Keys.POTION_EFFECTS).get();
        return potionEffects.stream().anyMatch(p -> p.getType() == type);
    }

    default void addPotionEffect(PotionEffect e) {
        T entity = getEntity();
        PotionEffectData orCreate = entity.getOrCreate(PotionEffectData.class).get();
        orCreate.addElement(e);
        entity.offer(orCreate);
    }


    default Location<World> getLocation() {
        return getEntity().getLocation();
    }

    default Vector3d getRotation() {
        return getEntity().getRotation();
    }

}
