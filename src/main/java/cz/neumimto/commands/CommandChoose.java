/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.commands;

import cz.neumimto.GroupService;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ResourceLoader;
import cz.neumimto.configuration.CommandLocalization;
import cz.neumimto.configuration.CommandPermissions;
import cz.neumimto.configuration.Localization;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.ActiveCharacter;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.Race;
import cz.neumimto.skills.ISkill;
import cz.neumimto.skills.SkillService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;;import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@ResourceLoader.Command
public class CommandChoose extends CommandBase {

    @Inject
    private CharacterService characterService;

    @Inject
    private GroupService groupService;

    @Inject
    private SkillService skillService;

    @Inject
    private NtRpgPlugin plugin;

    public CommandChoose() {
        setUsage(CommandLocalization.COMMAND_CHOOSE_USAGE);
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
      //     if (!commandSource.hasPermission(CommandPermissions.CANT_CHOOSE_CLASS)) {
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
                player.sendMessage(Texts.of(Localization.PLAYER_CHOOSED_CLASS.replaceAll("%1", nClass.getName())));
                return CommandResult.success();
        //   }
        //   commandSource.sendMessage(Texts.of(Localization.NO_PERMISSIONS));
        } else if (args[0].equalsIgnoreCase("race")) {
        //    if (!commandSource.hasPermission(CommandPermissions.CANT_CHOOSE_RACE)) {
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
                        player.sendMessage(Texts.of(Localization.PLAYER_CHOOSED_RACE.replaceAll("%1", r.getName())));
                        return CommandResult.success();
                    }
                    player.sendMessage(Texts.of(Localization.PLAYER_CANT_CHANGE_RACE));
                }
          //  }
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
                //todo skilltreecommand.class
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
            } else if (a.equalsIgnoreCase("learn")) {
                int i = characterService.characterLearnskill(character, skill, character.getPrimaryClass().getnClass().getSkillTree());
            } else if (a.equalsIgnoreCase("refund")) {
                if (PluginConfig.CAN_REFUND_SKILL) {
                    int i = characterService.refundSkill(character, skill, clazz);
                }
            }
        } else if (args[0].equalsIgnoreCase("character")) {
            if (args.length != 2) {
                commandSource.sendMessage(getUsage(commandSource));
                return CommandResult.success();
            }
            IActiveCharacter current = characterService.getCharacter(player.getUniqueId());
            if (current.getName().equalsIgnoreCase(args[1])) {
                player.sendMessage(Texts.of(Localization.ALREADY_CUURENT_CHARACTER));
                return CommandResult.empty();
            }
            Sponge.getScheduler().createTaskBuilder().async().name("GetCharacterList-" + player.getUniqueId())
                    .execute(() -> {
                        List<CharacterBase> playersCharacters = characterService.getPlayersCharacters(player.getUniqueId());
                        for (CharacterBase playersCharacter : playersCharacters) {
                            if (playersCharacter.getName().equalsIgnoreCase(args[1])) {
                                ActiveCharacter character = characterService.buildActiveCharacterAsynchronously(player, playersCharacter);
                                Sponge.getScheduler().createTaskBuilder().name("SetCharacterCallback"+player.getUniqueId())
                                        .execute(() -> characterService.setActiveCharacter(player.getUniqueId(), character))
                                        .submit(plugin);
                            }
                        }
                        player.sendMessage(Texts.of(Localization.NON_EXISTING_CHARACTER));
                    }).submit(plugin);
            return CommandResult.success();
        }
        commandSource.sendMessage(getUsage(commandSource));
        return CommandResult.success();
    }
}
