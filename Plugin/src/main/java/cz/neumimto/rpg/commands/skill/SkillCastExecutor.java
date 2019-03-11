package cz.neumimto.rpg.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.mods.SkillExecutorCallback;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SkillCastExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
		args.<ISkill>getOne(Text.of("skill")).ifPresent(iSkill -> {
			PlayerSkillContext info = character.getSkillInfo(iSkill.getId());
			if (info == PlayerSkillContext.Empty || info == null) {
				src.sendMessage(Localizations.CHARACTER_DOES_NOT_HAVE_SKILL.toText(Arg.arg("skill", iSkill.getName())));
			}
			NtRpgPlugin.GlobalScope.skillService.executeSkill(character, info, new SkillExecutorCallback() {
				@Override
				public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
					switch (skillResult.getResult()) {
						case ON_COOLDOWN:
							break;
						case NO_MANA:
							Gui.sendMessage(character, Localizations.NO_MANA, Arg.EMPTY);
							break;
						case NO_HP:
							Gui.sendMessage(character, Localizations.NO_HP, Arg.EMPTY);
							break;
						case CASTER_SILENCED:
							Gui.sendMessage(character, Localizations.PLAYER_IS_SILENCED, Arg.EMPTY);
							break;
						case NO_TARGET:
							Gui.sendMessage(character, Localizations.NO_TARGET, Arg.EMPTY);
					}
				}
			});
		});
		return CommandResult.success();
	}
}
