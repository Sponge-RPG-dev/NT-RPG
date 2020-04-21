package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.common.entity.AbstractEntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.SpigotMob;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import java.util.UUID;

public class MythicalMobsExpansion implements Listener {

    public void init(SpigotEntityService spigotEntityService) {
        spigotEntityService.setEntityHandler(new MythicalMobsHandler());
    }

    //If the mob is managed via MythicalMobs we simply want to skip
    public static class MythicalMobsHandler extends AbstractEntityService.EntityHandler<SpigotMob> {

        @Override
        public SpigotMob initializeEntity(MobSettingsDao dao, UUID uuid, SpigotMob iEntity, String dimmName, String id) {
            if (MythicMobs.inst().getAPIHelper().isMythicMob(uuid)) {
                return iEntity;
            }
            return super.initializeEntity(dao, uuid, iEntity, dimmName, id);
        }

        @Override
        public double getExperiences(MobSettingsDao dao, UUID uuid, String dimension, String type) {
            if (MythicMobs.inst().getAPIHelper().isMythicMob(uuid)) {
                Entity entity = Bukkit.getServer().getEntity(uuid);
                ActiveMob mythicMobInstance = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity);
                return mythicMobInstance.getType().getConfig().getDouble("ntrpg.experiences", 0);
            }
            return super.getExperiences(dao, uuid, dimension, type);
        }

        @Override
        public boolean handleMobDamage(UUID uuid) {
            return !MythicMobs.inst().getAPIHelper().isMythicMob(uuid);
        }
    }
}
