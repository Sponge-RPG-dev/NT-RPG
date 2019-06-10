package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
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
                String msg = Rpg.get().getLocalizationService().translate(LocalizationKeys.CANNOT_BIND_NON_EXECUTABLE_SKILL);
                src.sendMessage(TextHelper.parse(msg));
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
