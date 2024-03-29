package cz.neumimto.rpg.spigot.bridges.mmoitems;

import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillConfigLoader;
import cz.neumimto.rpg.common.utils.DebugLevel;
import net.Indyuce.mmoitems.skill.RegisteredSkill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MMOItemWrapperFactory {


    public static List<ISkill> generateSkills(Collection<RegisteredSkill> abilities) {
        List<ISkill> list = new ArrayList<>();
        SkillConfigLoader skillConfigLoader = new SkillConfigLoader("mmoitems", MMOItemSkill.class);

        Log.info("Found MMOItems " + abilities.size() + " abilities generating ntrpg skills, !! its not guaranteed all of these skills will work flawlessly ingame");

        for (RegisteredSkill ability : abilities) {
            try {
                list.add(generateSkill(ability, skillConfigLoader));
            } catch (IllegalArgumentException e) {
                Log.error("Could not generate skill wrapper for " + ability.getName() + " skipping", e);
            }

        }
        return list;
    }

    public static ISkill generateSkill(RegisteredSkill ability, SkillConfigLoader skillConfigLoader) {

        String lowerCaseID = "mmoitems:" + ability.getName().replaceAll(" ", "").replaceAll("-", "");

        MMOItemSkill skill = null;
        try {
            Log.info(" - Generating skill " + lowerCaseID, DebugLevel.DEVELOP);
            skill = (MMOItemSkill) skillConfigLoader.generateClass(lowerCaseID).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        skill.setAbility(ability);
        Log.info(" - Settings: " + lowerCaseID, DebugLevel.BALANCE);
        Set<String> modifiers = skill.getAbility().getHandler().getModifiers();
        for (String s : modifiers) {
            if (s.equals("mana") || s.equals("stamina") || s.equals("cooldown")) {
                continue;
            }
            Log.info("  - " + s, DebugLevel.BALANCE);
        }
        return skill;
    }
}

