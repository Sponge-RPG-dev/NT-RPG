package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;

public class SkillUpgrade extends PassiveSkill {
    public SkillUpgrade() {
        super.type = Type.UPGRADE;
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {

    }

}
