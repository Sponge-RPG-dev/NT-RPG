package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.effects.negative.Bleeding;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.Decorator;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 5.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:bandage")
public class Bandage extends Targeted {

    @Inject
    private EntityService entityService;

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.HEALED_AMOUNT, 15, 5);
        addSkillType(SkillType.HEALING);
        addSkillType(SkillType.PHYSICAL);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        if (target.isFriendlyTo(source)) {
            float floatNodeValue = skillContext.getFloatNodeValue(SkillNodes.HEALED_AMOUNT);
            entityService.healEntity(target, floatNodeValue, this);
            ISpongeEntity entity = (ISpongeEntity) target;
            Decorator.healEffect(entity.getEntity().getLocation().add(0, 1, 0));
            if (target.hasEffect(Bleeding.name)) {
                effectService.removeEffectContainer(target.getEffect(Bleeding.name), target);
            }
            return SkillResult.OK;
        }
        return SkillResult.CANCELLED;
    }
}
