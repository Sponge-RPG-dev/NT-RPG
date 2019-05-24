package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SkillRefundExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<ClassDefinition>getOne(Text.of("class")).ifPresent(aClass -> {
            args.<ISkill>getOne("skill").ifPresent(iSkill -> {
                Player player = (Player) src;
                if (aClass.getSkillTree() != null) {
                    IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
                    PlayerClassData playerClassData = character.getClasses().get(aClass.getName());
                    aClass.getSkillTreeType().processRefundSkill(character, playerClassData, iSkill);
                } else {
                    player.sendMessage(Localizations.CLASS_HAS_NO_SKILLTREE.toText(Arg.arg("class", aClass.getName())));
                }
            });
        });
        return CommandResult.success();
    }
}
