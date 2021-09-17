package cz.neumimto.rpg.spigot.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.Location;

import javax.inject.Singleton;

@Singleton
public class SpawnLightning {

    public void spawn(IEntity entity) {
        ISpigotEntity e = (ISpigotEntity) entity;
        Location location = e.getEntity().getLocation();
        location.getWorld().strikeLightningEffect(location);
    }
}
