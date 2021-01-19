package cz.neumimto.rpg.sponge.skills.passive;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.sponge.effects.ManaDrainEffect;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:drain")
public class Drain extends PassiveSkill {

    @Inject
    private EffectService effectService;

    public Drain() {
        super(ManaDrainEffect.name);
        settings.addNode(SkillNodes.AMOUNT, 1);
        setDamageType(DamageTypes.ATTACK.getId());
        addSkillType(SkillType.HEALTH_DRAIN);
        addSkillType(SkillType.DRAIN);
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
        double floatNodeValue = info.getFloatNodeValue(SkillNodes.AMOUNT);
        ManaDrainEffect effect = new ManaDrainEffect(character, -1L, floatNodeValue);
        effectService.addEffect(effect, this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(IActiveCharacter, level, context);
        PlayerSkillContext info = IActiveCharacter.getSkill(getId());
        double floatNodeValue = info.getFloatNodeValue(SkillNodes.AMOUNT);
        IEffectContainer<Double, ManaDrainEffect> container = IActiveCharacter.getEffect(ManaDrainEffect.name);
        container.updateValue(floatNodeValue, this);
    }
}
