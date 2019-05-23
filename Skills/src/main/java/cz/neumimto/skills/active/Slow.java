package cz.neumimto.skills.active;

import cz.neumimto.effects.negative.SlowPotion;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:slow")
public class Slow extends Targeted {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.DURATION, 5000, 100);
        settings.addNode(SkillNodes.AMPLIFIER, 1, 2);
    }

    @Override
    public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        int i = skillContext.getIntNodeValue(SkillNodes.AMPLIFIER);
        SlowPotion effect = new SlowPotion(target, duration, i);
        effectService.addEffect(effect, this);
        skillContext.next(source, info, skillContext.result(SkillResult.OK));
    }
}
