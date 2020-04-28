package cz.neumimto.rpg.spigot.bridges.mmoitems;

import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.common.skills.SkillConfigLoader;
import net.Indyuce.mmoitems.api.ability.Ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MMOItemWrapperFactory {


    public static List<ISkill> generateSkills(Collection<Ability> abilities) {
        List<ISkill> list = new ArrayList<>();
        SkillConfigLoader skillConfigLoader = new SkillConfigLoader("mmoitems", MMOItemSkill.class);

        Log.info("Found MMOItems " + abilities.size() + " abilities generating ntrpg skills, !! its not guaranteed all of these skills will work flawlessly ingame");

        for (Ability ability : abilities) {
            try {
                list.add(generateSkill(ability, skillConfigLoader));
            } catch (IllegalArgumentException e) {
                Log.error("Could not generate skill wrapper for " + ability.getID() + " skipping", e);
            }

        }
        return list;
    }

    public static ISkill generateSkill(Ability ability, SkillConfigLoader skillConfigLoader) {

        String lowerCaseID = "mmoitems:" + ability.getLowerCaseID().replaceAll("-","");

        MMOItemSkill skill = null;
        try {
            Log.info(" - Generating skill " + lowerCaseID, DebugLevel.DEVELOP);
            skill = (MMOItemSkill) skillConfigLoader.generateClass(lowerCaseID).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        skill.setAbility(ability);
        Log.info(" - Settings: " + lowerCaseID , DebugLevel.BALANCE);
        for (String s : skill.getAbility().getModifiers()) {
            if (s.equals("mana") || s.equals("stamina") ||s.equals("cooldown")) {
                continue;
            }
            Log.info("  - " + s, DebugLevel.BALANCE);
        }
        return skill;
    }
}

