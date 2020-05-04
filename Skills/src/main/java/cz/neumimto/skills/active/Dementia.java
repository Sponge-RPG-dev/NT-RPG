package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.AllSkillsBonus;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 10.8.17.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:dementia")
public class Dementia extends Targeted {

    @Inject
    private EntityService entityService;

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.DURATION, 30000, 1500);
        settings.addNode("skill-level", 1, 2);
        addSkillType(SkillType.DISEASE);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        int skillLevel = skillContext.getIntNodeValue("skill-level");
        AllSkillsBonus bonus = new AllSkillsBonus(target, duration, -1 * skillLevel);
        effectService.addEffect(bonus, this);
        return SkillResult.OK;
    }
}
