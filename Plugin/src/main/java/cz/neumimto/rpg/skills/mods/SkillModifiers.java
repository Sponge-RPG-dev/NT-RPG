package cz.neumimto.rpg.skills.mods;

import static cz.neumimto.rpg.skills.mods.ModTargetExcution.AFTER;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillModifiers {

    public static SkillModProcessor MANA_BURN = new SkillModProcessor("mana_burn", AFTER) {

        @Override
        public void process(IActiveCharacter iActiveCharacter, SkillModList copy, ExtendedSkillInfo info) {

        }
    };
}
