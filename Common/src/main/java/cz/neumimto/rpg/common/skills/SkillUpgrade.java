package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.types.PassiveSkill;

public class SkillUpgrade extends PassiveSkill {
    public SkillUpgrade() {
        super.type = Type.UPGRADE;
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {

    }

}
