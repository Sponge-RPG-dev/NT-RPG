package cz.neumimto.rpg.commands.skill;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

public class SkillBindExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Optional<ISkill> skill = args.getOne("skill");
		if (skill.isPresent()) {
			ISkill iSkill = skill.get();
			if (!(iSkill instanceof ActiveSkill)) {
				src.sendMessage(Localizations.CANNOT_BIN_NON_EXECUTABLE_SKILL.toText());
				return CommandResult.empty();
			}
			Player pl = (Player) src;
			IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(pl);
			if (character.isStub()) {
				return CommandResult.empty();
			}
			ItemStack is = NtRpgPlugin.GlobalScope.inventorySerivce.createSkillbind(iSkill);
			pl.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class)).offer(is);
		}

		return CommandResult.success();
	}
}
