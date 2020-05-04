package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.skills.types.Targeted;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:lightning")
public class SkillLightning extends Targeted {

    @Override
    public void init() {
        super.init();
        setDamageType(NDamageType.LIGHTNING.getId());
        settings.addNode(SkillNodes.DAMAGE, 10, 20);
        settings.addNode(SkillNodes.RANGE, 10, 10);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.LIGHTNING);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
        SkillDamageSource s = new SkillDamageSourceBuilder()
                .fromSkill(this)
                .setSource(source)
                .build();
        ISpongeEntity entity = (ISpongeEntity) target.getEntity();
        entity.getEntity().damage(damage, s);
        Decorator.strikeLightning(entity.getEntity());
        return SkillResult.OK;
    }
}
