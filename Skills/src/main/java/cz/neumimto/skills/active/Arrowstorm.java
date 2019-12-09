package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.ArrowstormEffect;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import javax.inject.Singleton;

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
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode("min-arrows", 35, 1);
        settings.addNode("max-arrows", 45, 1);
        settings.addNode(SkillNodes.PERIOD, 100, -10);
        addSkillType(SkillType.PHYSICAL);
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
    }

    @Override
    public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        int min = skillContext.getIntNodeValue("min-arrows");
        int max = skillContext.getIntNodeValue("max-arrows");
        int arrows = ThreadLocalRandom.current().nextInt(max - min) + min;
        min = skillContext.getIntNodeValue(SkillNodes.PERIOD);
        min = min <= 0 ? 1 : min;
        effectService.addEffect(new ArrowstormEffect(character, min, arrows), this);
        skillContext.next(character, info, skillContext.result(SkillResult.OK));
    }
}
