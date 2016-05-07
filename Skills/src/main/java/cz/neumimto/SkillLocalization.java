package cz.neumimto;

import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;

/**
 * Created by NeumimTo on 23.12.2015.
 */

@ConfigurationContainer(path = "./mods/NtRpg/", filename = "Skillsloc.conf")
public class SkillLocalization {

    @ConfigValue
    public static String SKILL_SPEED_DESC = "Boosts caster's movement speed";

    @ConfigValue
    public static String SKILL_TELEPORT_DESC = "Teleports caster to a targetted block" ;

    @ConfigValue
    public static String SKILL_LIGHTNING_DESC = "Hits targetted enemy with a lightning bolt";

    @ConfigValue
    public static String SKILL_JUMP_DESC = "Launches caster into air";

    @ConfigValue
    public static String SKILL_FIREBALL_DESC = "Casts a fireball";

    @ConfigValue
    public static String SKILL_FIREBALL_LORE = "The Sorceress can collect a large amount of elemental fire :n and contain it within a globe of energy. :n Discharged toward the enemy, those energies are :n released in a devastating explosion";

    @ConfigValue
    public static String SKILL_LIGHTNING_LORE = "This spell allows a Sorceress to summon the power of the heavens :n and emit a tremendous surge of electrical energy.";

    @ConfigValue
    public static String SKILL_SOULBIND_LORE = "..";

    @ConfigValue
    public static String SKILL_SOULBIND_DESC = "..";

    @ConfigValue
    public static String SKILL_BRAINSAP_DESC = "..";

    @ConfigValue
    public static String SKILL_BRAINSAP_LORE = "..";
}
