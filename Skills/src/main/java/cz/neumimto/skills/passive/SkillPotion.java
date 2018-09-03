package cz.neumimto.skills.passive;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.neumimto.effects.positive.PotionEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@ResourceLoader.Skill("ntrpg:potion")
public class SkillPotion extends PassiveSkill {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public SkillPotion() {
		super(PotionEffect.name);
		Map<PotionEffectType, Long> list = new HashMap<>();
		Collection<PotionEffectType> allOf = Sponge.getRegistry().getAllOf(PotionEffectType.class);
		for (PotionEffectType type : allOf) {
			list.put(type, 19000L);
		}
		SkillSettings settings = new SkillSettings();
		settings.addNode("cooldown-reduced", 0, -125);
		settings.addObjectNode("potions", gson.toJson(list));
	}

	@Override
	public void skillLearn(IActiveCharacter IActiveCharacter) {
		super.skillLearn(IActiveCharacter);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
	}


	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
		PotionEffect pe = (PotionEffect) character.getEffect(PotionEffect.name);
		if (pe == null) {
			String potions = info.getSkillData().getSkillSettings().getObjectNode("potions");
			//todo
		}
	}
}
