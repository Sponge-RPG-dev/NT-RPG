package cz.neumimto.rpg.commands.admin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AddEffectExecutor implements CommandExecutor {
	private static Gson gson = new Gson();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player = args.<Player>getOne("player").get();
		IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
		Long k = args.<Long>getOne("duration").get();

		Optional<String> dataOptional = args.getOne("data");
		Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
		EffectParams map = new EffectParams();

		if (!dataOptional.isPresent()) {
			if (modelType != Void.TYPE)
				throw new CommandException(Text.of("Effect data expected! Use ? as data to list parameters"));
		} else {
			String s = dataOptional.get();
			if (s.equals("?")) {
				if (modelType == Void.TYPE) {
					src.sendMessage(Text.of("No data expected"));
					return CommandResult.empty();
				} else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
					src.sendMessage(Text.of("Expected: " + modelType.getTypeName()));
					return CommandResult.empty();
				} else {
					Map<String, String> q = new HashMap<>();
					for (Field field : modelType.getFields()) {
						q.put(field.getName(), field.getType().getName());
					}
					src.sendMessage(Text.of("Expected: " + gson.toJson(q)));
					return CommandResult.empty();
				}
			}
			if (modelType == Void.TYPE) {
				//Just do nothing
			} else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
				map.put("value", s);
			} else try {
				//Get rid of unused entries in data string and check for missing
				EffectParams tempMap = gson.fromJson(s, EffectParams.class);
				for (Field field : modelType.getFields()) {
					if (Modifier.isTransient(field.getModifiers())) continue;
					if (!tempMap.containsKey(field.getName())) {
						throw new CommandException(Text.of("Missing parameter: " + field.getName()));
					}
					map.put(field.getName(), tempMap.get(field.getName()));
				}
			} catch (JsonSyntaxException e) {
				Map<String, String> q = new HashMap<>();
				for (Field field : modelType.getFields()) {
					q.put(field.getName(), field.getType().getName());
				}
				throw new CommandException(Text.of("Expected: " + gson.toJson(q)));
			}
		}

		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
		if (NtRpgPlugin.GlobalScope.effectService.addEffect(effect.construct(character, k, map), InternalEffectSourceProvider.INSTANCE)) {
			src.sendMessage(Text.of("Effect " + effect.getName() + " applied to player " + player.getName()));
			return CommandResult.success();
		}
		else return CommandResult.empty();
	}
}
