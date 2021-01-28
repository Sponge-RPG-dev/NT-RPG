package cz.neumimto.rpg.spigot.skills.utils;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

@ResourceLoader.Skill("ntrpg:megabolt")
public class Megabolt extends ActiveSkill<IEntity> {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public SkillResult cast(IEntity caster, PlayerSkillContext info) {
        double doubleNodeValue1 = info.getDoubleNodeValue(SkillNodes.RANGE);
        double doubleNodeValue = info.getDoubleNodeValue(SkillNodes.DAMAGE);
        return null;
    }
}
