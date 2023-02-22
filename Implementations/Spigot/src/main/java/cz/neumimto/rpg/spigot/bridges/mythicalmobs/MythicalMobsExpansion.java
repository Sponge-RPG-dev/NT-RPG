package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.entity.AbstractEntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.SpigotMob;
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

public class MythicalMobsExpansion implements Listener {

    @Inject
    private Injector injector;

    @Inject
    private SpigotSkillService spigotSkillService;

    public void init(SpigotEntityService spigotEntityService) {
        spigotEntityService.setEntityHandler(new MythicalMobsHandler());

        Collection<ISkill> iSkills = MythicalMobsWrapperFactory.generateSkills(MythicBukkit.inst().getSkillManager().getSkills());
        for (ISkill iSkill : iSkills) {
            injector.injectMembers(iSkill);
            spigotSkillService.registerAdditionalCatalog(iSkill);
        }
    }

    //If the mob is managed via MythicalMobs we simply want to skip
    public static class MythicalMobsHandler extends AbstractEntityService.EntityHandler<SpigotMob> {

        @Override
        public double getExperiences(MobSettingsDao dao, String dimName, String type, UUID uuid) {
            if (MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)) {
                Entity entity = Bukkit.getServer().getEntity(uuid);
                ActiveMob mythicMobInstance = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
                return mythicMobInstance.getType().getConfig().getDouble("ntrpg.experiences", 0);
            }
            return super.getExperiences(dao, dimName, type, uuid);
        }
    }
}
