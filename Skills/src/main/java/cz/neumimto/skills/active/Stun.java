package cz.neumimto.skills.active;

import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.types.Targeted;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ja on 20.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:stun")
public class Stun extends Targeted {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.DAMAGE, 10, 1);
        settings.addNode(SkillNodes.DURATION, 4500, 100);
        addSkillType(SkillType.PHYSICAL);
        addSkillType(SkillType.MOVEMENT);
        setDamageType(DamageTypes.ATTACK.getId());
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        StunEffect stunEffect = new StunEffect(target, duration);
        effectService.addEffect(stunEffect, this, source);
        if (damage > 0) {
            SkillDamageSource s = new SkillDamageSourceBuilder()
                    .fromSkill(this)
                    .setSource(source)
                    .build();
            ((ISpongeEntity) target).getEntity().damage(damage, s);
        }
        return SkillResult.OK;
    }

}
