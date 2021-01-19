package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 1.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:conductivity")
public class Conductivity extends ActiveSkill {

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.DURATION, 10000);
        settings.addNode(SkillNodes.RADIUS, 10);
        settings.addNode(SkillNodes.RANGE, 15);
        addSkillType(SkillType.CURSE);
        addSkillType(SkillType.DECREASED_RESISTANCE);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext modifier) {
        return SkillResult.OK;
    }
}
