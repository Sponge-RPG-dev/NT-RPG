package cz.neumimto.rpg.commands.admin;

import com.google.gson.Gson;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class AddEffectExecutor implements CommandExecutor {
	private static Gson gson = new Gson();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player = args.<Player>getOne("player").get();
		IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
		Long k = args.<Long>getOne("duration").get();
		String data = args.<String>getOne("data").get();
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
		EffectParams params = gson.fromJson(data, EffectParams.class);
		if (NtRpgPlugin.GlobalScope.effectService.addEffect(effect.construct(character, k, params), InternalEffectSourceProvider.INSTANCE))
			return CommandResult.success();
		else return CommandResult.empty();
	}
}
