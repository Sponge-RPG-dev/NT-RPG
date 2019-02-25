package cz.neumimto.skills.passive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.ManaDrainEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
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
		settings.addNode(SkillNodes.AMOUNT, 1, 1);
		setDamageType(DamageTypes.ATTACK);
		addSkillType(SkillType.HEALTH_DRAIN);
		addSkillType(SkillType.DRAIN);
	}

	@Override
	public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
		int totalLevel = info.getTotalLevel();
		float floatNodeValue =  info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.AMOUNT, totalLevel);
		ManaDrainEffect effect = new ManaDrainEffect(character, -1L, floatNodeValue);
		effectService.addEffect(effect, character, this);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
		PlayerSkillContext info = IActiveCharacter.getSkill(getId());
		int totalLevel = info.getTotalLevel();
		float floatNodeValue =  info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.AMOUNT, totalLevel);
		IEffectContainer<Float, ManaDrainEffect> container = IActiveCharacter.getEffect(ManaDrainEffect.name);
		container.updateValue(floatNodeValue, this);
	}
}
