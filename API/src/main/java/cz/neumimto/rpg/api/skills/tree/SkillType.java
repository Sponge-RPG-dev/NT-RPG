/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.api.skills.tree;


import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.utils.TriState;

import static cz.neumimto.rpg.api.utils.TriState.*;

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
}
