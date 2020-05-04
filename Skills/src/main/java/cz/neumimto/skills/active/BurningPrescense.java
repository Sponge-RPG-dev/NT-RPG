package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.BurningPrescenseEffect;
import cz.neumimto.model.BPModel;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.skills.NDamageType;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:burningprescense")
public class BurningPrescense extends ActiveSkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.PERIOD, 1000, -10);
        settings.addNode(SkillNodes.RADIUS, 3, 0);
        settings.addNode(SkillNodes.DAMAGE, 5, 1);
        setDamageType(NDamageType.FIRE.getId());
        addSkillType(SkillType.AURA);
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.FIRE);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext skillContext) {
        if (character.hasEffect(BurningPrescenseEffect.name)) {
            effectService.removeEffectContainer(character.getEffect(BurningPrescenseEffect.name), character);
        } else {
            BPModel model = getBPModel(skillContext);
            model.duration = -1;
            BurningPrescenseEffect eff = new BurningPrescenseEffect(character, -1, model);
            effectService.addEffect(eff, this);
        }

        return SkillResult.OK;
    }

    private BPModel getBPModel(PlayerSkillContext skillContext) {
        BPModel model = new BPModel();
        model.period = skillContext.getIntNodeValue(SkillNodes.PERIOD);
        model.radius = skillContext.getLongNodeValue(SkillNodes.RADIUS);
        model.damage = skillContext.getIntNodeValue(SkillNodes.DAMAGE);
        return model;
    }
}
