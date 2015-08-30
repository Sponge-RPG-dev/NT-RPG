package cz.neumimto.commands;

import cz.neumimto.gui.Gui;
import cz.neumimto.configuration.CommandLocalization;
import cz.neumimto.configuration.CommandPermissions;
import cz.neumimto.configuration.Localization;
import cz.neumimto.ioc.Command;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.CharacterService;
import cz.neumimto.skills.ExtendedSkillInfo;
import cz.neumimto.skills.SkillService;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by NeumimTo on 23.7.2015.
 */
@Command
public class SkillExecuteCommand extends CommandBase {

    @Inject
    private SkillService skillService;

    @Inject
    private CharacterService characterService;

    public SkillExecuteCommand() {
        setPermission(CommandPermissions.CHARACTER_EXECUTE_SKILL_PERMISSION);
        setDescription(CommandLocalization.COMMAND_SKILL_DESC);
        alias.add("skill");
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        IActiveCharacter character = characterService.getCharacter(((Player)commandSource).getUniqueId());
        if (character.isStub()) {
            commandSource.sendMessage(Texts.of(Localization.CHARACTER_IS_REQUIRED));
            return CommandResult.empty();
        }
        String[] a = s.split(" ");
        ExtendedSkillInfo info = character.getSkillInfo(a[0]);
        if (info == ExtendedSkillInfo.Empty || info == null) {
            commandSource.sendMessage(Texts.of(Localization.CHARACTER_DOES_NOT_HAVE_SKILL));
        }
        int i = skillService.executeSkill(character, info);
        if (i == 2) {
            Gui.sendMessage(character, Localization.ON_COOLDOWN);
        }
        return CommandResult.empty();
    }
}
