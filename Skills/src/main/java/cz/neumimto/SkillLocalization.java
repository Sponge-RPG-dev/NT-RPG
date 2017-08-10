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

    @ConfigValue
    public static String STR = "Strength";

    @ConfigValue
    public static String STR_DESC = "";

    @ConfigValue
    public static String INT_DESC  = "";

    @ConfigValue
    public static String AGI_DESC = "";

    @ConfigValue
    public static String INT = "Intelligence";

    @ConfigValue
    public static String AGI = "Agility";

    @ConfigValue
    public static String Arrowstorm = "Shoots a random number of arrows";

    @ConfigValue
    public static String basher;

    @ConfigValue
    public static String burningPrescense = "";

    @ConfigValue
    public static String burningPrescense_desc = "";

    @ConfigValue
    public static String SKILL_MULTIBOLT_DESC = "";

    @ConfigValue
    public static String SKILL_MULTIBOLT_LORE = "";

    @ConfigValue
    public static String SKILL_DODGE_LORE = "";;

    @ConfigValue
    public static String SKILL_DODGE_DESC = "";;

    @ConfigValue
    public static String SKILL_RESOLUTE_TECHNIQUE_DESC = "";

    @ConfigValue
    public static String SKILL_RESOLUTE_TECHNIQUE_LORE = "";

    @ConfigValue
    public static String SKILL_WRESTLE_LORE = "";

    @ConfigValue
    public static String SKILL_WRESTLE_DESC = "";

    @ConfigValue
    public static String SKILL_DRAIN_DESC = "";

    @ConfigValue
    public static String SKILL_DRAIN_LORE = "";

    @ConfigValue
    public static String SKILL_CRITICAL_DESC = "";

    @ConfigValue
    public static String SKILL_CRITICAL_LORE = "";

    @ConfigValue
    public static String SKILL_EMPHATY_DESC = "";

    @ConfigValue
    public static String SKILL_EMPHATY_LORE = "";

    @ConfigValue
    public static String SKILL_DAMPEN_DESC = "";

    @ConfigValue
    public static String SKILL_DAMPEN_LORE = "";

    @ConfigValue
    public static String TELEPORTATION_SCROLL = "Teleportation Scroll";

    @ConfigValue
    public static String SKILL_PORTAL_DESC = "Creates a portal to another location";

    @ConfigValue
    public static String SKILL_PORTAL_NAME = "Portal";

    @ConfigValue
    public static String CONDUCTIVITY_NAME = "Conductivity";

    @ConfigValue
    public static String CONDUCTIVITY_DESC = "Decreases lightning resistance of entities in area";

    @ConfigValue
    public static String ASTRONOMY_NAME = "Astronomy";

    @ConfigValue
    public static String ASTRONOMY_DESC = "Ability to print current coordinates onto a peper, which can be later used for redirecting a portal";

    @ConfigValue
    public static String ASTRONOMY_CANNOT_SEE_THE_SKY = "You cannot see the sky from current location";

    @ConfigValue
    public static String SKILL_JUMP_NAME = "Jump";

    @ConfigValue
    public static String GRAPPLING_HOOK_NAME = "GrapplingHook";

    @ConfigValue
    public static String GRAPPLING_HOOK_DESC = "";

    @ConfigValue
    public static String Skill_BANDAGE_DESC = "Heals the target, or a caster";

    @ConfigValue
    public static String SKILL_BANDAGE_NAME = "Bandage";

    @ConfigValue
    public static String SKILL_GROUPHEAL_NAME = "GroupHeal";

    @ConfigValue
    public static String SKILL_GROUPHEAL_DESC = "Heals nearby allies";

    @ConfigValue
    public static String SKILL_PANDEMIC_NAME = "Pandemic";

    @ConfigValue
    public static String SKILL_PANDEMIC_DESC = "Drains health from nearby entities";

    @ConfigValue
    public static String SKILL_BATTLECHARGE_DESC = "Speed boost for your party";

    @ConfigValue
    public static String SKILL_BATTLECHARGE_NAME = "BattleCharge";

    @ConfigValue
    public static String SKILL_ENDERPEARL_NAME = "EnderPeaerl";

    @ConfigValue
    public static String SKILL_ENDERPEARL_DESC = "Ability to use ender pearls to teleport";

    @ConfigValue
    public static String SKILL_DEMENTIA_DESC = "Decreases level of all skills of the target";

    @ConfigValue
    public static String SKILL_DEMENTIA_NAME = "Dementia";
}
