package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class PlayerClassCommandElement extends PatternMatchingCommandElement {

	public PlayerClassCommandElement(@Nullable Text key) {
		super(key);
	}

	@Override
	protected Iterable<String> getChoices(CommandSource source) {
		Player player = (Player) source;
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
		return character.getClasses().keySet();
	}

	@Override
	protected Object getValue(String choice) throws IllegalArgumentException {
		return NtRpgPlugin.GlobalScope.classService.getClassDefinitionByName(choice);
	}
}
