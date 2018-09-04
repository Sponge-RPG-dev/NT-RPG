package cz.neumimto.rpg.utils;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.skills.tree.SkillTreeSpecialization;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.Source;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Set;

/**
 * Created by NeumimTo on 25.8.2018.
 */
public class Placeholders {

	@Inject
	CharacterService characterService;

	@Placeholder(id = "race")
	public Text getRace(@Source Player src) {
		Race race = characterService.getCharacter(src).getRace();
		return Text.of(race.getPreferedColor(), race.getName());
	}

	@Placeholder(id = "class")
	public Text getClass(@Source Player src) {
		ExtendedNClass primaryClass = characterService.getCharacter(src).getPrimaryClass();
		return Text.of(primaryClass.getConfigClass().getPreferedColor(), primaryClass.getConfigClass().getName());
	}

	@Placeholder(id = "char_name")
	public Text getCharName(@Source Player src) {
		IActiveCharacter character = characterService.getCharacter(src);
		return Text.of(character.getName());
	}

	@Placeholder(id = "primary_class_or_spec")
	public Text getClassOrSpec(@Source Player src) {
		IActiveCharacter character = characterService.getCharacter(src);
		ExtendedNClass primaryClass = character.getPrimaryClass();
		Set<SkillTreeSpecialization> skillTreeSpecialization = character.getSkillTreeSpecialization();
		//todo
		return Text.of(primaryClass.getConfigClass().getPreferedColor(), primaryClass.getConfigClass().getName());
	}

	@Placeholder(id = "primary_class_level")
	public Integer getPrimaryClassLevel(@Source Player src) {
		IActiveCharacter character = characterService.getCharacter(src);
		return character.getLevel();
	}

	@Placeholder(id = "mana")
	public Double getMana(@Source Player src) {
		IActiveCharacter character = characterService.getCharacter(src);
		return character.getMana().getValue();
	}

	@Placeholder(id = "max_mana")
	public Double getMaxMana(@Source Player src) {
		IActiveCharacter character = characterService.getCharacter(src);
		return character.getMana().getMaxValue();
	}

	@Placeholder(id = "max_hp")
	public Double getMaxHealth(@Source Player src) {
		IActiveCharacter character = characterService.getCharacter(src);
		return character.getHealth().getMaxValue();
	}

}
