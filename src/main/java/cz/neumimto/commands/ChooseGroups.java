package cz.neumimto.commands;

import cz.neumimto.GroupService;
import cz.neumimto.configuration.CommandLocalization;
import cz.neumimto.configuration.CommandPermissions;
import cz.neumimto.configuration.Localization;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.ioc.Command;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.Race;
import cz.neumimto.skills.ISkill;
import cz.neumimto.skills.SkillService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Command
public class ChooseGroups extends CommandBase {

    @Inject
    private CharacterService characterService;

    @Inject
    private GroupService groupService;

    @Inject
    private SkillService skillService;

    public ChooseGroups() {
        setUsage(CommandLocalization.COMMAND_CHOOSEGROUP_USAGE);
        setDescription(CommandLocalization.COMMAND_CHOOSE_DESC);
        setPermission(CommandPermissions.COMMAND_CHOOSE_ACCESS);
        addAlias(CommandPermissions.CHOOSEGROUP_ALIAS);
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        String[] args = s.split(" ");
        if (!(commandSource instanceof Player)) {
            return CommandResult.success();
        }
        Player player = (Player) commandSource;
        if (args[0].equalsIgnoreCase("class")) {
            if (commandSource.hasPermission(CommandPermissions.COMMAND_CHOOSE_CLASS)) {
                NClass nClass = groupService.getNClass(args[1].toLowerCase());
                if (nClass == NClass.Default) {
                    player.sendMessage(Texts.of(Localization.NON_EXISTING_GROUP));
                    return CommandResult.empty();
                }
                int i = 0;
                if (args.length == 3) {
                    i = Integer.parseInt(args[2]) - 1;
                }
                if (i < 0) {
                    i = 0;
                }
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.isStub()) {
                    player.sendMessage(Texts.of(Localization.CHARACTER_IS_REQUIRED));
                    return CommandResult.empty();
                }
                characterService.updatePlayerGroups(character, nClass, i, null, null);
                return CommandResult.success();
            }
            commandSource.sendMessage(Texts.of(Localization.NO_PERMISSIONS));
        } else if (args[0].equalsIgnoreCase("race")) {
            if (commandSource.hasPermission(CommandPermissions.COMMAND_CHOOSE_RACE)) {
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.isStub()) {
                    player.sendMessage(Texts.of(Localization.CHARACTER_IS_REQUIRED));
                    return CommandResult.empty();
                }
                Race r = groupService.getRace(args[1]);
                if (r == Race.Default) {
                    player.sendMessage(Texts.of(Localization.NON_EXISTING_GROUP));
                    return CommandResult.empty();
                }
                if (character.getRace() == Race.Default || (character.getRace() != Race.Default && PluginConfig.PLAYER_CAN_CHANGE_RACE)) {
                    if (PluginConfig.PLAYER_CAN_CHANGE_RACE) {
                        characterService.updatePlayerGroups(character, null, 0, r, null);
                        return CommandResult.success();
                    }
                    player.sendMessage(Texts.of(Localization.PLAYER_CANT_CHANGE_RACE));
                }
            }
        } else if (args[0].equalsIgnoreCase("skill")) {
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                player.sendMessage(Texts.of(Localization.CHARACTER_IS_REQUIRED));
                return CommandResult.success();
            }
            final String a = args[1];
            ISkill skill = skillService.getSkill(args[2]);
            NClass clazz = null;
            if (args.length == 4) {

            } else {
                clazz = character.getPrimaryClass().getnClass();
            }
            if (skill == null) {
                commandSource.sendMessage(Texts.of(Localization.SKILL_DOES_NOT_EXIST));
                return CommandResult.success();
            }
            if (a.equalsIgnoreCase("upgrade")) {
                int i = characterService.upgradeSkill(character, skill);
                return CommandResult.success();
            } else if (a.equalsIgnoreCase("take")) {
                int i = characterService.characterLearnskill(character, skill, character.getPrimaryClass().getnClass().getSkillTree());
            } else if (a.equalsIgnoreCase("refund")) {
                if (PluginConfig.CAN_REFUND_SKILL) {
                    int i = characterService.refundSkill(character, skill, clazz);
                }
            }
        } else {
            commandSource.sendMessage(getUsage(commandSource));
        }
        return CommandResult.success();
    }
}
