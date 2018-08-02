package cz.neumimto.skills.passive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.ManaDrainEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ResourceLoader.Skill("ntrpg:drain")
public class Drain extends PassiveSkill {

	@Inject
	private EffectService effectService;

	public Drain() {
		super(ManaDrainEffect.name);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.AMOUNT, 1, 1);
		super.settings = settings;
		setDamageType(DamageTypes.ATTACK);
		addSkillType(SkillType.HEALTH_DRAIN);
		addSkillType(SkillType.DRAIN);
	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		float floatNodeValue = getFloatNodeValue(info, SkillNodes.AMOUNT);
		ManaDrainEffect effect = new ManaDrainEffect(character, -1L, floatNodeValue);
		effectService.addEffect(effect, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
		ExtendedSkillInfo skill = IActiveCharacter.getSkill(getId());
		float floatNodeValue = getFloatNodeValue(skill, SkillNodes.AMOUNT);
		IEffectContainer<Float, ManaDrainEffect> container = IActiveCharacter.getEffect(ManaDrainEffect.name);
		container.updateValue(floatNodeValue, this);
	}
}
