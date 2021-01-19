package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.effects.positive.SpeedBoost;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:speed")
public class SkillSpeed extends ActiveSkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        setDamageType(null);
        settings.addNode(SkillNodes.DURATION, 1000);
        settings.addNode(SkillNodes.AMOUNT, 0.1f);
        addSkillType(SkillType.MOVEMENT);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext skillContext) {
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        float amount = skillContext.getFloatNodeValue(SkillNodes.AMOUNT);
        SpeedBoost sb = new SpeedBoost(character, duration, amount);
        effectService.addEffect(sb, this);
        return SkillResult.OK;
    }
}
