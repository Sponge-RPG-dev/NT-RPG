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

package cz.neumimto.rpg.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.CommandPermissions;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.properties.PlayerPropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.SkillTreeActionResult;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

;

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

    @Inject
    private DamageService damageService;

    @Inject
    private PlayerPropertyService playerPropertyService;

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
        if (args.length == 0) {
            Gui.invokeDefaultMenu(characterService.getCharacter(player.getUniqueId()));
        } else if (args[0].equalsIgnoreCase("class")) {
            //     if (!commandSource.hasPermission(CommandPermissions.CANT_CHOOSE_CLASS)) {
            ConfigClass configClass = groupService.getNClass(args[1].toLowerCase());
            if (configClass == ConfigClass.Default) {
                player.sendMessage(Text.of(Localization.NON_EXISTING_GROUP));
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
                player.sendMessage(Text.of(Localization.CHARACTER_IS_REQUIRED));
                return CommandResult.empty();
            }
            if (character.getRace() == Race.Default) {
                player.sendMessage(Text.of(Localization.RACE_IS_REQUIRED));
                return CommandResult.empty();
            }
            if (!character.getRace().getAllowedClasses().contains(configClass)) {
                player.sendMessage(Text.of(Localization.RACE_AND_CLASS_CONFLICT
                        .replaceAll("%1", character.getRace().getName()).replaceAll("%2", configClass.getName())));
                return CommandResult.empty();
            }
            characterService.updatePlayerGroups(character, configClass, i, null, null);
            player.sendMessage(Text.of(Localization.PLAYER_CHOOSED_CLASS.replaceAll("%1", configClass.getName())));
            return CommandResult.success();
            //   }
            //   commandSource.sendMessage(Texts.of(Localization.NO_PERMISSIONS));
        } else if (args[0].equalsIgnoreCase("race")) {
            //    if (!commandSource.hasPermission(CommandPermissions.CANT_CHOOSE_RACE)) {
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                player.sendMessage(Text.of(Localization.CHARACTER_IS_REQUIRED));
                return CommandResult.empty();
            }
            Race r = groupService.getRace(args[1]);
            if (r == Race.Default) {
                player.sendMessage(Text.of(Localization.NON_EXISTING_GROUP));
                return CommandResult.empty();
            }
            if (character.getRace() == Race.Default || (character.getRace() != Race.Default && PluginConfig.PLAYER_CAN_CHANGE_RACE)) {
                if (PluginConfig.PLAYER_CAN_CHANGE_RACE) {
                    characterService.updatePlayerGroups(character, null, 0, r, null);
                    player.sendMessage(Text.of(Localization.PLAYER_CHOOSED_RACE.replaceAll("%1", r.getName())));
                    return CommandResult.success();
                }
                player.sendMessage(Text.of(Localization.PLAYER_CANT_CHANGE_RACE));
            }
            //  }
        } else if (args[0].equalsIgnoreCase("skill")) {
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                player.sendMessage(Text.of(Localization.CHARACTER_IS_REQUIRED));
                return CommandResult.success();
            }
            final String a = args[1];
            ISkill skill = skillService.getSkill(args[2]);
            ConfigClass clazz = null;
            if (args.length == 4) {
                //todo skilltreecommand.class
            } else {
                clazz = character.getPrimaryClass().getConfigClass();
            }
            if (skill == null) {
                commandSource.sendMessage(Text.of(Localization.SKILL_DOES_NOT_EXIST));
                return CommandResult.success();
            }
            if (a.equalsIgnoreCase("upgrade")) {
                Pair<SkillTreeActionResult, SkillTreeActionResult.Data> data = characterService.upgradeSkill(character, skill);

                player.sendMessage(Text.of(data.value.bind(data.key.message)));
                return CommandResult.success();
            } else if (a.equalsIgnoreCase("learn")) {
                Pair<SkillTreeActionResult, SkillTreeActionResult.Data> data = characterService.characterLearnskill(character, skill, character.getPrimaryClass().getConfigClass().getSkillTree());
                player.sendMessage(Text.of(data.value.bind(data.key.message)));
            } else if (a.equalsIgnoreCase("refund")) {
                if (PluginConfig.CAN_REFUND_SKILL) {
                    int i = characterService.refundSkill(character, skill, clazz);
                }
            }
        } else if (args[0].equalsIgnoreCase("attribute")) {
            if (args.length != 3) {
                commandSource.sendMessage(getUsage(commandSource));
                return CommandResult.empty();
            }
            int i;
            if (Utils.isNumeric(args[2])) {
                i = Integer.parseInt(args[2]);
                if (i <= 0) {
                    commandSource.sendMessage(Text.of(Localization.ARGUMENT_MUST_BE_POSITIVE_INT));
                    return CommandResult.empty();
                }
            } else {
                commandSource.sendMessage(Text.of(Localization.ARGUMENT_MUST_BE_POSITIVE_INT));
                return CommandResult.empty();
            }
            ICharacterAttribute attribute = playerPropertyService.getAttribute(args[1]);
            IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
            characterService.addAttribute(character, attribute, i);
            characterService.putInSaveQueue(character.getCharacterBase());

        } else if (args[0].equalsIgnoreCase("character")) {
            if (args.length != 2) {
                commandSource.sendMessage(getUsage(commandSource));
                return CommandResult.success();
            }
            IActiveCharacter current = characterService.getCharacter(player.getUniqueId());
            if (current.getName().equalsIgnoreCase(args[1])) {
                player.sendMessage(Text.of(Localization.ALREADY_CUURENT_CHARACTER));
                return CommandResult.empty();
            }
            Sponge.getScheduler().createTaskBuilder().async().name("GetCharacterList-" + player.getUniqueId())
                    .execute(() -> {
                        List<CharacterBase> playersCharacters = characterService.getPlayersCharacters(player.getUniqueId());
                        boolean b = false;
                        for (CharacterBase playersCharacter : playersCharacters) {
                            if (playersCharacter.getName().equalsIgnoreCase(args[1])) {
                                ActiveCharacter character = characterService.buildActiveCharacterAsynchronously(player, playersCharacter);
                                Sponge.getScheduler().createTaskBuilder().name("SetCharacterCallback" + player.getUniqueId())
                                        .execute(() -> characterService.setActiveCharacter(player.getUniqueId(), character))
                                        .submit(plugin);
                                b = true;
                            }
                        }
                        if (!b)
                            player.sendMessage(Text.of(Localization.NON_EXISTING_CHARACTER));
                    }).submit(plugin);
            return CommandResult.success();
        }
        commandSource.sendMessage(getUsage(commandSource));
        return CommandResult.success();
    }
}
