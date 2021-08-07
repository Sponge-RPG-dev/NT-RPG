package cz.neumimto.rpg.spigot.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.Location;

import javax.inject.Singleton;

@Singleton
@TargetSelector("spawn_lightning")
public class SpawnLightning {

    @Handler
    public void spawn(IEntity entity) {
        ISpigotEntity e = (ISpigotEntity) entity;
        Location location = e.getEntity().getLocation();
        location.getWorld().strikeLightningEffect(location);
    }
}
