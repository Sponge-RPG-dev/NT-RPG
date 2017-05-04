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
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.CommandPermissions;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Guild;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 23.7.2015.
 */
@ResourceLoader.Command
public class CommandCreate extends CommandBase {

    @Inject
    CharacterService characterService;

    @Inject
    Game game;

    @Inject
    NtRpgPlugin plugin;

    @Inject
    GroupService groupService;


    @Inject
    private InventoryService inventoryService;

    @Inject
    private SkillService skillService;

    public CommandCreate() {
        addAlias(CommandPermissions.COMMAND_CREATE_ALIAS);
        setUsage(CommandLocalization.COMMAND_CREATE_USAGE);
        setDescription(CommandLocalization.COMMAND_CREATE_DESCRIPTION);
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        if (commandSource instanceof Player) {
            String[] args = s.split(" ");
            if (args[0].equalsIgnoreCase("character")) {
                game.getScheduler().createTaskBuilder().async().execute(() -> {
                    Player player = (Player) commandSource;
                    int i = characterService.canCreateNewCharacter(player.getUniqueId(), args[1]);
                    if (i == 1) {
                        commandSource.sendMessage(Text.of(Localization.REACHED_CHARACTER_LIMIT));
                    } else if (i == 2) {
                        commandSource.sendMessage(Text.of(Localization.CHARACTER_EXISTS));
                    } else if (i == 0) {
                        CharacterBase characterBase = new CharacterBase();
                        characterBase.setName(args[1]);
                        characterBase.setRace(Race.Default.getName());
                        characterBase.setPrimaryClass(ConfigClass.Default.getName());
                        CharacterClass characterClass = new CharacterClass();
                        characterClass.setName(ConfigClass.Default.getName());
                        characterClass.setExperiences(0D);
                        characterClass.setCharacterBase(characterBase);
                        characterBase.setAttributePoints(PluginConfig.ATTRIBUTEPOINTS_ON_START);
                        characterBase.getCharacterClasses().add(characterClass);
                        characterBase.setUuid(player.getUniqueId());
                        characterBase.setAttributePoints(PluginConfig.ATTRIBUTEPOINTS_ON_START);
                        characterService.createAndUpdate(characterBase);
                        commandSource.sendMessage(Text.of(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));

                        Gui.sendListOfCharacters(characterService.getCharacter(player.getUniqueId()),characterBase);

                    }
                }).submit(plugin);
            } else if (args[0].equalsIgnoreCase("party")) {
                if (!commandSource.hasPermission(CommandPermissions.PARTY_CREATE)) {
                    commandSource.sendMessage(Text.of(Localization.NO_PERMISSIONS));
                    return CommandResult.empty();
                }

                IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
                if (character.isStub()) {
                    Gui.sendMessage(character, Localization.CHARACTER_IS_REQUIRED);
                    return CommandResult.success();
                }
                if (character.hasParty()) {
                    Gui.sendMessage(character, Localization.ALREADY_IN_PARTY);
                    return CommandResult.success();
                }
                Party party = new Party(character);
                character.setParty(party);
                Gui.sendMessage(character, Localization.PARTY_CREATED);
            } else if (args[0].equalsIgnoreCase("guild")) {
                String name = args[1];
                Guild guild = new Guild();
                guild.setName(name);
            } else if (args[0].equalsIgnoreCase("bind")) {
                IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
                if (args.length <= 2) {
                    character.sendMessage("/create bind [r {skillname}] [l {skillname}]");
                    return CommandResult.empty();
                }
                if (!character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                    ISkill r = null;
                    ISkill l = null;
                    for (int i = 1; i < args.length; i += 2) {
                        if (args[i].equalsIgnoreCase("r")) {
                            r = skillService.getSkill(args[i + 1]);
                        } else if (args[i].equalsIgnoreCase("l")) {
                            l = skillService.getSkill(args[i + 1]);
                        }
                    }
                    ItemStack i = ItemStack.of(InventoryService.ITEM_SKILL_BIND, 1);
                    inventoryService.createHotbarSkill(i, r, l);
                    character.getPlayer().setItemInHand(HandTypes.MAIN_HAND,i);
                } else {
                    character.getPlayer().sendMessage(Text.of(Localization.EMPTY_HAND_REQUIRED));
                }
            }
        } else {
            commandSource.sendMessage(Text.of("This command can't be executed from console."));
        }
        return CommandResult.success();
    }
}
