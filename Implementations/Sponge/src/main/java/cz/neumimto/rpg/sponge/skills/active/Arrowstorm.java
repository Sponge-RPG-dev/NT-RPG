package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.effects.positive.ArrowstormEffect;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:arrowstorm")
public class Arrowstorm extends ActiveSkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        setDamageType(DamageTypes.PROJECTILE.getId());
        settings.addNode(SkillNodes.DAMAGE, 10);
        settings.addExpression("min-arrows", 35);
        settings.addExpression("max-arrows", 45);
        settings.addNode(SkillNodes.PERIOD, 100);
        addSkillType(SkillType.PHYSICAL);
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext skillContext) {
        int min = skillContext.getIntNodeValue("min-arrows");
        int max = skillContext.getIntNodeValue("max-arrows");
        int arrows = ThreadLocalRandom.current().nextInt(max - min) + min;
        min = skillContext.getIntNodeValue(SkillNodes.PERIOD);
        min = min <= 0 ? 1 : min;
        effectService.addEffect(new ArrowstormEffect(character, min, arrows), this);
        return SkillResult.OK;
    }
}
