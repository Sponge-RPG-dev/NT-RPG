package cz.neumimto.rpg.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CommandblockSkillExecutor;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.mods.SkillExecutorCallback;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.CommandBlockMinecart;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SkillCastExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		IActiveCharacter character = null;
		if (src instanceof Player) {
			character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
		} else if (src instanceof CommandBlockSource) {
			if (src instanceof CommandBlockMinecart) {
				//TODO: do something (does somebody really use minecart commandblocks?)
			} else {
				Optional<TileEntity> tileOptional = ((CommandBlockSource) src).getLocation().getTileEntity();
				if (tileOptional.isPresent() && tileOptional.get() instanceof CommandBlock) {
					CommandBlock cmdBlock = (CommandBlock) tileOptional.get();
					character = CommandblockSkillExecutor.wrap(cmdBlock);
				}
			}
		}
		if (character == null) return CommandResult.empty(); //Failed

		ISkill skill = args.<ISkill>getOne(Text.of("skill")).get();

		PlayerSkillContext info = character.getSkillInfo(skill.getId());
		if (info == PlayerSkillContext.Empty || info == null) {
			src.sendMessage(Localizations.CHARACTER_DOES_NOT_HAVE_SKILL.toText(Arg.arg("skill", skill.getName())));
			//TODO: maybe return?
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
		return CommandResult.success();
	}
}
