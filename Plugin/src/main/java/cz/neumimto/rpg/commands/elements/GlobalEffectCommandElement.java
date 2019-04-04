package cz.neumimto.rpg.commands.elements;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class GlobalEffectCommandElement extends PatternMatchingCommandElement {

	public GlobalEffectCommandElement(@Nullable Text key) {
		super(key);
	}

	@Override
	protected Iterable<String> getChoices(CommandSource source) {
		return NtRpgPlugin.GlobalScope.effectService.getGlobalEffects()
				.keySet()
				.stream()
				.map(this::normalize).collect(Collectors.toList());
	}

	private String normalize(String s) {
		return s.replaceAll("_", " ");
	}

	@Override
	protected Object getValue(String choice) throws IllegalArgumentException {
		choice = choice.replaceAll("_", " ");
		return NtRpgPlugin.GlobalScope.effectService.getGlobalEffects().get(choice);
	}


	@Override
	public Text getUsage(CommandSource src) {
		return Text.of("<effect>");
	}

}

