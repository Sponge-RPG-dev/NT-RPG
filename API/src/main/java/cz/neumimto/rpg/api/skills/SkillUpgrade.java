package cz.neumimto.rpg.api.skills;

import com.typesafe.config.Config;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

public class SkillUpgrade extends PassiveSkill {
    public SkillUpgrade() {
        super.type = Type.UPGRADE;
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {

    }

}
