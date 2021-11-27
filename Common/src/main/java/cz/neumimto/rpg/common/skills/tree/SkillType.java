

package cz.neumimto.rpg.common.skills.tree;


import cz.neumimto.rpg.common.skills.ISkillType;
import cz.neumimto.rpg.common.utils.TriState;

import static cz.neumimto.rpg.common.utils.TriState.*;

/**
 * Created by NeumimTo on 26.7.2015.
 */
//Todo make name translatable
public enum SkillType implements ISkillType {
    PHYSICAL(UNDEFINED, "ntrpg:physical", "Physical"),
    HEALING(FALSE, "ntrpg:healing", "Healing"),
    SUMMON(UNDEFINED, "ntrpg:summon", "Summon"),
    PROJECTILE(UNDEFINED, "ntrpg:projectile", "Projectile"),
    CAN_CAST_WHILE_SILENCED(FALSE, "ntrpg:can_cast_while_silenced", "Can cast while silenced"),
    UTILITY(UNDEFINED, "ntrpg:utility", "Utility"),
    HEALTH_DRAIN(TRUE, "ntrpg:health_drain", "Health_drain"),
    AURA(UNDEFINED, "ntrpg:aura", "Aura"),
    CURSE(TRUE, "ntrpg:curse", "Curse"),
    AOE(UNDEFINED, "ntrpg:aoe", "Aoe"),
    DECREASED_RESISTANCE(UNDEFINED, "ntrpg:decreased_resistance", "Decreased resistance"),
    ESCAPE(FALSE, "ntrpg:escape", "Escape"),
    TELEPORT(UNDEFINED, "ntrpg:teleport", "Teleport"),
    STEALTH(FALSE, "ntrpg:stealth", "Stealth"),
    MOVEMENT(UNDEFINED, "ntrpg:movement", "Movement"),
    DISEASE(TRUE, "ntrpg:disease", "Disease"),
    FIRE(UNDEFINED, "ntrpg:fire", "Fire"),
    ELEMENTAL(UNDEFINED, "ntrpg:elemental", "Elemental"),
    LIGHTNING(UNDEFINED, "ntrpg:lightning", "Lightning"),
    ICE(UNDEFINED, "ntrpg:ice", "Ice"),
    DRAIN(TRUE, "ntrpg:drain", "Drain"),
    CANNOT_BE_SELF_CASTED(UNDEFINED, "ntrpg:cannot_be_self_casted", "Cannot be self casted"),
    PROTECTION(TRUE, "ntrpg:protection", "Protection"),
    ILLUSION(FALSE, "ntrpg:illusion", "Illusion"),
    BUFF(FALSE,"ntrpg:buff", "Buff"),
    DAMAGE_CHECK_TARGET(TRUE, "ntrpg:damage_check_target", "Negative"),
    /**
     * Do not use this one in custom skills
     */
    PATH(UNDEFINED, "ntrpg:path", "Path");


    private TriState negative;
    private String id;
    private String name;

    SkillType(TriState negative, String id, String name) {
        this.negative = negative;
        this.id = id;
        this.name = name;
    }

    @Override
    public TriState isNegative() {
        return negative;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTranslationKey() {
        return name;
    }

    public static SkillType byId(String id) {
        for (SkillType value : values()) {
            if (value.id.equalsIgnoreCase(id)) {
                return value;
            }
        }
        return null;
    }
}
