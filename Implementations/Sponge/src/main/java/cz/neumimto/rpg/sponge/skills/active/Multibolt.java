package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.effects.negative.MultiboltEffect;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.model.MultiboltModel;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by NeumimTo on 6.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:multibolt")
public class Multibolt extends Targeted {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        setDamageType(NDamageType.LIGHTNING.getId());
        settings.addNode(SkillNodes.DAMAGE, 10);
        settings.addNode("times-hit", 10);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.LIGHTNING);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
        int timesToHit = skillContext.getIntNodeValue("times-hit");
        MultiboltModel model = new MultiboltModel(timesToHit, damage);
        IEffect effect = new MultiboltEffect(target, source, model);
        effectService.addEffect(effect, this);
        return SkillResult.OK;
    }
}
