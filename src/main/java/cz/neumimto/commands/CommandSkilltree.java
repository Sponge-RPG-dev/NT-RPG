package cz.neumimto.commands;

import cz.neumimto.GroupService;
import cz.neumimto.configuration.Localization;
import cz.neumimto.gui.Gui;
import cz.neumimto.ioc.Command;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.StartingPoint;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by ja on 31.8.2015.
 */
@Command
public class CommandSkilltree extends CommandBase {
    @Inject
    private GroupService groupService;

    @Inject
    private CharacterService characterService;

    public CommandSkilltree() {

    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        if (commandSource instanceof Player) {
            String[] args = s.split(" ");
            NClass nClass = null;
            SkillInfo skillInfo = null;
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equalsIgnoreCase("class")) {
                    nClass = groupService.getNClass(args[i + 1]);
                    if (nClass == NClass.Default) {
                        commandSource.sendMessage(Texts.of(Localization.NON_EXISTING_GROUP));
                        return CommandResult.empty();
                    }
                    if (args[i].equalsIgnoreCase("skill")) {
                        skillInfo = nClass.getSkillTree().getSkills().get(args[i + 1]);
                        if (skillInfo == SkillInfo.EMPTY) {
                            commandSource.sendMessage(Texts.of(Localization.SKILL_DOES_NOT_EXIST));
                            return CommandResult.empty();
                        }
                    }
                }
            }
            Player player = (Player) commandSource;
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                player.sendMessage(Texts.of(Localization.CHARACTER_IS_REQUIRED));
            }
            if (nClass == null) {
                nClass = character.getPrimaryClass().getnClass();
            }
            if (skillInfo == null) {
                skillInfo = nClass.getSkillTree().getSkills().get(StartingPoint.name);
            }
            Gui.moveSkillTreeMenu(character, nClass.getSkillTree(), character.getCharacterBase().getSkills(), skillInfo);
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
