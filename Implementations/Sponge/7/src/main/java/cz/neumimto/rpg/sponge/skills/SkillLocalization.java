package cz.neumimto.rpg.sponge.skills;


import cz.neumimto.rpg.api.localization.Localization;

/**
 * Created by NeumimTo on 23.12.2015.
 */

@Localization({
        "localizations/ntrpg_skills_cs.properties",
        "localizations/ntrpg_skills_en.properties"
})
public class SkillLocalization {

    public static String TELEPORTATION_SCROLL = "skill.teleportation-scroll.label";
    public static String ASTRONOMY_CANNOT_SEE_THE_SKY = "skill.astronomy-obstucted-sky-view.message";
    public static String TELEPORT_LOCATION_OBSTRUCTED = "skill.cannot-teleport.message";
    public static String CANNOT_DRIK_POTION_TYPE = "skill.cannot-drink-potion.message";
    public static String CANNOT_DRIK_POTION_TYPE_COOLDOWN = "skill.cannot-drink-potion-cooldown.message";
}
