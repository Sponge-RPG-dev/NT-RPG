package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.entity.AbstractEntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.SpigotMob;
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class MythicalMobsExpansion implements Listener {

    @Inject
    private Injector injector;

    @Inject
    private SpigotSkillService spigotSkillService;

    public void init(SpigotEntityService spigotEntityService) {
        spigotEntityService.setEntityHandler(new MythicalMobsHandler());

        List<ISkill> iSkills = MythicalMobsWrapperFactory.generateSkills(MythicMobs.inst().getSkillManager().getSkills());
        for (ISkill iSkill : iSkills) {
            injector.injectMembers(iSkill);
            spigotSkillService.registerAdditionalCatalog(iSkill);
        }
    }

    //If the mob is managed via MythicalMobs we simply want to skip
    public static class MythicalMobsHandler extends AbstractEntityService.EntityHandler<SpigotMob> {

        @Override
        public double getExperiences(MobSettingsDao dao, String dimName, String type, UUID uuid) {
            if (MythicMobs.inst().getAPIHelper().isMythicMob(uuid)) {
                Entity entity = Bukkit.getServer().getEntity(uuid);
                ActiveMob mythicMobInstance = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity);
                return mythicMobInstance.getType().getConfig().getDouble("ntrpg.experiences", 0);
            }
            return super.getExperiences(dao, dimName, type, uuid);
        }
    }
}
