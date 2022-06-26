package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillConfigLoader;
import cz.neumimto.rpg.common.utils.DebugLevel;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MythicalMobsWrapperFactory {

    public static List<ISkill> generateSkills(Collection<Skill> abilities) {
        List<ISkill> list = new ArrayList<>();
        SkillConfigLoader skillConfigLoader = new SkillConfigLoader("mythicmobs", MythicalMobsSkill.class);

        Log.info("Found MythicMobs " + abilities.size() + " skills generating ntrpg skills, !! its not guaranteed all of these skills will work flawlessly ingame");

        Collection<Skill> skills = MythicBukkit.inst().getSkillManager().getSkills();

        for (Skill skill : skills) {
            list.add(generateSkill(skill, skillConfigLoader));
        }


        return list;
    }

    public static ISkill generateSkill(Skill mskill, SkillConfigLoader skillConfigLoader) {

        String lowerCaseID = "mythicmobs:" + mskill.getInternalName().toLowerCase();

        MythicalMobsSkill skill = null;
        try {
            Log.info(" - Generating skill " + lowerCaseID, DebugLevel.DEVELOP);
            skill = (MythicalMobsSkill) skillConfigLoader.generateClass(lowerCaseID).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        skill.setMmSkill(mskill);

        return skill;
    }

}
