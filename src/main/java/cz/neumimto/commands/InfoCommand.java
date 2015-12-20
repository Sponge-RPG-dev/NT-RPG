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
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.gui.Gui;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.PlayerGroup;
import cz.neumimto.players.groups.Race;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

/**
 * Created by NeumimTo on 23.7.2015.
 */
@ResourceLoader.Command
public class InfoCommand extends CommandBase {

    @Inject
    Game game;

    @Inject
    private CharacterService characterService;

    @Inject
    private NtRpgPlugin plugin;

    @Inject
    private GroupService groupService;

    public InfoCommand() {
        setHelp(CommandLocalization.PLAYERINFO_HELP);
        setPermission(CommandPermissions.COMMANDINFO_PERMS);
        setDescription(CommandLocalization.PLAYERINFO_DESC);
        setUsage(CommandLocalization.PLAYERINFO_USAGE);
        addAlias(CommandPermissions.COMMANDINFO_ALIAS);
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        final String[] args = s.split(" ");
        if (args.length == 0) {
            commandSource.sendMessage(getUsage(commandSource));
            return CommandResult.empty();
        }
        if (args[0].equalsIgnoreCase("player")) {
            if (args.length != 2) {
                commandSource.sendMessage(Texts.of(getUsage(commandSource)));
                return CommandResult.success();
            }
            Optional<Player> o = game.getServer().getPlayer(args[1]);
            if (o.isPresent()) {
                Player player = o.get();
                printPlayerInfo(commandSource, args, player);
                return CommandResult.success();
            } else {
                commandSource.sendMessage(Texts.of(Localization.PLAYER_IS_OFFLINE_MSG));
            }
        } else if (args[0].equalsIgnoreCase("race")) {
            printRaceInfor(commandSource, args);
        } else if (args[0].equalsIgnoreCase("races")) {
            printRaceList(commandSource, "race");
        } else if (args[0].equalsIgnoreCase("guilds")) {
            printGuildList(commandSource, "guild");
        } else if (args[0].equalsIgnoreCase("character")) {
            if (!(commandSource instanceof Player)) {
                if (args.length != 2) {
                    Player player = (Player) commandSource;
                    IActiveCharacter target = characterService.getCharacter(player.getUniqueId());
                    Gui.showCharacterInfo(target, target);
                }
            }
        } else if (args[0].equalsIgnoreCase("runes")) {

        } else if (args[0].equalsIgnoreCase("stats")) {
            Player player = (Player) commandSource;
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            String q = "HP: "+character.getHealth().getValue()+"/"+character.getHealth().getMaxValue()+"/"+character.getHealth().getRegen();
            player.sendMessage(Texts.of(q));
            q = "Mana: "+character.getMana().getValue()+"/"+character.getMana().getMaxValue()+"/"+character.getMana().getRegen();
            player.sendMessage(Texts.of(q));
        }
        return CommandResult.success();
    }

    //TODO create inventory menus
    private void printGuildList(CommandSource commandSource, String nextcmd) {
        Collection<? extends PlayerGroup> group = groupService.getGuilds();
        printList(commandSource, group, nextcmd);
    }

    private void printList(CommandSource commandSource, Collection<? extends PlayerGroup> group, String nextcmd) {
        TextBuilder builder = Texts.builder();
        List<Text> texts = new ArrayList<>();
        for (PlayerGroup g : group) {
            if (!g.showsInMenu()) {
                continue;
            }
            texts.add(builder.append(Texts.of(g.getName() + ", "))
                    .onHover(TextActions.showText(Texts.of("Get more info on click")))
                    .onClick(TextActions.runCommand(Texts.of("/" + getAliases().get(0)) + " race " + g.getName()))
                    .build());

            builder.removeAll();
        }
        commandSource.sendMessage(Texts.join(texts));
    }

    private void printRaceList(CommandSource commandSource, String nextcmd) {
        Collection<? extends PlayerGroup> group = groupService.getRaces();
        printList(commandSource, group, nextcmd);
    }

    private void printRaceInfor(CommandSource commandSource, String[] args) {
        Race race = groupService.getRace(args[1]);
        if (race == Race.Default) {
            commandSource.sendMessage(Texts.of(Localization.NON_EXISTING_GROUP));
            return;
        }
        Text text = Texts.of();
        TextBuilder builder = text.builder().append(Texts.of("Name : " + race.getName()))
                .append(Texts.of("Weapons :    "));
        for (Map.Entry<ItemType, Double> e : race.getWeapons().entrySet()) {
            builder.append(Texts.of(e.getValue())).append(Texts.of(", "));
        }
        builder.append(Texts.of("Armor  :    "));
        for (ItemType s : race.getAllowedArmor()) {
            builder.append(Texts.of(s.toString()));
        }
        commandSource.sendMessage(builder.build());
    }

    private void printPlayerInfo(CommandSource commandSource, String[] args, Player player) {
        game.getScheduler().createTaskBuilder().async().execute(() -> {
            List<CharacterBase> characters = characterService.getPlayersCharacters(player.getUniqueId());
            if (characters.isEmpty()) {
                commandSource.sendMessage(Texts.of("Player has no characters"));
                return;
            }
            for (CharacterBase character : characters) {
                Text build = Texts.builder()
                        .append(Texts.of(character.getName()))
                        .onHover(TextActions.showText(Texts.of(getSmallInfo(character))))
                        .build();
                commandSource.sendMessage(build);
            }
        }).submit(plugin);
    }

    private String getSmallInfo(CharacterBase character) {
        return TextColors.GOLD + "L: " + character.getLevel() + ", C:" + character.getPrimaryClass() + ", R: " + character.getRace() + ", G: " + character.getGuild();
    }


}
