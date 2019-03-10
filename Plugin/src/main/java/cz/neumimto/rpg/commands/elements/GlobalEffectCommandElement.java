package cz.neumimto.rpg.commands.elements;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.IGlobalEffect;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class GlobalEffectCommandElement extends CommandElement {

	public GlobalEffectCommandElement(@Nullable Text key) {
		super(key);
	}

	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String effectName = args.next().replaceAll("_", " ");
		IGlobalEffect effect = NtRpgPlugin.GlobalScope.effectService.getGlobalEffect(effectName);
		if (effect == null) {
			throw args.createError(TextSerializers.FORMATTING_CODE.deserialize("&CUnknown effect &C\"" + effectName + "\""));
		}
		return effect;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		return NtRpgPlugin.GlobalScope.effectService.getGlobalEffects()
				.keySet()
				.stream()
				.map(a -> a.replaceAll(" ", "_"))
				.filter(a -> {
					try {
						return a.toLowerCase().startsWith(args.next());
					} catch (ArgumentParseException ignored) {
					}
					return false;
				})
				.collect(Collectors.toList());
	}

	@Override
	public Text getUsage(CommandSource src) {
		return Text.of("<effect>");
	}

}

