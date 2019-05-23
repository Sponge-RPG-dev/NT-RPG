package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.FissureEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:fissure")
public class Fissure extends ActiveSkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        setDamageType(null);
        settings.addNode(SkillNodes.DURATION, 8000, 2);
        settings.addNode(SkillNodes.RANGE, 10, 1);
    }

    @Override
    public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext context) {
        long duration = context.getLongNodeValue(SkillNodes.DURATION);
        int range = context.getIntNodeValue(SkillNodes.RANGE);
        FissureEffect effect = new FissureEffect(character, duration, range);
        effectService.addEffect(effect, this);
        context.next(character, info, context);
    }
}
