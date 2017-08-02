package cz.neumimto.rpg;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ActiveSkill;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillModifier;
import cz.neumimto.rpg.skills.SkillResult;

/**
 * Created by NeumimTo on 2.8.2017.
 */
public class ParticleSkillShot extends ActiveSkill {
    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
        return null;
    }
}
