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

package cz.neumimto.rpg.sponge.commands;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.commands.admin.*;
import cz.neumimto.rpg.sponge.commands.arguments.CommandSkillArgument;
import cz.neumimto.rpg.sponge.commands.character.CharacterShowClassExecutor;
import cz.neumimto.rpg.sponge.commands.character.CharacterShowClassesExecutor;
import cz.neumimto.rpg.sponge.commands.elements.*;
import cz.neumimto.rpg.sponge.commands.item.*;
import cz.neumimto.rpg.sponge.commands.skill.*;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class CommandService {

    @Inject
    private SpongeRpgPlugin plugin;

    @Inject
    private Injector injector;

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SpongeItemService itemService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private RWService rwService;

    public void registerStandartCommands() {
        registerAdminCommands();
        registerCharacterCommands();
        registerItemCommands();
    }

    private Text translate(String key) {
        return TextHelper.parse(localizationService.translate(key));
    }
    
    private void registerAdminCommands() {

        //==========SKILLS AND EFFECTS==========

        CommandSpec executeSkill = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_EXEC_SKILL_DESC))
                .arguments(
                        new CommandSkillArgument(Text.of("skill")),
                        GenericArguments.flags().valueFlag(GenericArguments.string(Text.of("loc")), "1")
                                .valueFlag(GenericArguments.string(Text.of("loc")), "1")
                                .valueFlag(GenericArguments.string(Text.of("head")), "2")
                                .valueFlag(GenericArguments.string(Text.of("settings")), "3")
                                .buildWith(GenericArguments.none())
                )
                .executor(new ExecuteSkillExecutor())
                .build();

        CommandSpec addEffect = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_EFFECT_ADD))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("player"))),
                        new GlobalEffectCommandElement(Text.of("effect")),
                        GenericArguments.longNum(Text.of("duration")),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("data")))
                )
                .executor(injector.getInstance(AddEffectExecutor.class))
                .build();

        //=========CHARACTER MANIPULATIONS=========

        CommandSpec experienceAdd = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_EXP_ADD))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("player"))),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.optionalWeak(GenericArguments.string(Text.of("source"))),
                        GenericArguments.optional(new ClassDefCommandElement(Text.of("class")))
                )
                .executor(injector.getInstance(AddExperienceExecutor.class))
                .build();

        CommandSpec experience = CommandSpec.builder()
                .child(experienceAdd, "add")
                .build();

        //==========UTILITY==========

        CommandSpec reload = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_RELOAD))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("args")))
                .executor(new ReloadExecutor())
                .build();

        CommandSpec inspectProperty = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("player"))),
                        GenericArguments.remainingJoinedStrings(Text.of("data"))
                )
                .executor(new InspectPropertyExecutor())
                .build();

        CommandSpec inspectItemDamage = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("player")))
                )
                .executor(new InspectItemDamageExecutor())
                .build();

        CommandSpec inspect = CommandSpec.builder()
                .child(inspectProperty, "property", "p")
                .child(inspectItemDamage, "itemDamage", "idmg")
                .build();


        CommandSpec invoke = CommandSpec.builder()
                .description(Text.builder("Command which let you execute commands as another player.")
                        .append(Text.builder("/nadmin invoke SomePlayer classes Primary").color(TextColors.LIGHT_PURPLE).build())
                        .append(Text.builder("will class selection GUI on the client side of the player SomePlayer. Does not bypass any permissions.").build()).build())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.remainingJoinedStrings(Text.of("command"))
                )
                .executor(new InvokeExecutorExecutor())
                .build();


        CommandSpec adminRoot = CommandSpec
                .builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_DESC))
                .permission("ntrpg.admin")
                .child(executeSkill, "skill", "s")
                .child(addEffect, "effect", "ef")

                .child(experience, "experiences", "exp")

                .child(reload, "reload")
                .child(inspect, "i", "inspect")
                .child(invoke, "invoke", "invk")
                .build();

        Sponge.getCommandManager().register(plugin, adminRoot, "nadmin", "na");
    }

    private void registerCharacterCommands() {

        //==========CHARACTER SKILLS==========

        CommandSpec skillBind = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_BIND_DESC))
                .permission("ntrpg.player.skillbind")
                .arguments(
                        new LearnedSkillCommandElement(Text.of("skill"))
                )
                .executor(new SkillBindExecutor())
                .build();

        Sponge.getCommandManager().register(plugin, skillBind, "bind", "nb");


        //==========GROUPS==========
        CommandSpec showClassWeapons = CommandSpec.builder()
                .arguments(new ClassDefCommandElement(Text.of("class")))
                .executor((src, args) -> {
                    args.<ClassDefinition>getOne(Text.of("class"))
                            .ifPresent(playerGroup -> {
                                Player player = (Player) src;
                                IActiveCharacter character = characterService.getCharacter(player);
                                Gui.displayClassWeapons(playerGroup, character);
                            });
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(plugin, showClassWeapons, "weapons", "wp");

        CommandSpec showClassArmors = CommandSpec.builder()
                .arguments(new ClassDefCommandElement(Text.of("class")))
                .executor((src, args) -> {

                    args.<ClassDefinition>getOne(Text.of("class"))
                            .ifPresent(playerGroup -> {
                                Player player = (Player) src;
                                IActiveCharacter character = characterService.getCharacter(player);
                                Gui.displayClassArmor(playerGroup, character);
                            });
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(plugin, showClassArmors, "armor");

        CommandSpec showRunes = CommandSpec.builder()
                .permission("ntrpg.runes.list")
                .executor((src, args) -> {
                    Gui.sendListOfRunes(characterService.getCharacter((Player) src));
                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(plugin, showRunes, "runes");
    }

    private void registerItemCommands() {
        //==========ITEMS==========

        CommandSpec itemAddGlobalEffect = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_ENCHANT_ADD))
                .arguments(
                        new GlobalEffectCommandElement(Text.of("effect")),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("data")))
                )
                .executor(new ItemAddGlobalEffectExecutor())
                .build();


        CommandSpec itemEnchant = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_ENCHANT))
                .child(itemAddGlobalEffect, "add", "e")
                .build();


        CommandSpec itemAddSocket = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_SOCKET))
                .arguments(
                        new CommandElementMapLookup(Text.of("type"),
                                () -> itemService.getSocketTypes())
                )
                .executor(new ItemAddSocketExecutor())
                .build();

        CommandSpec itemAddRune = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_RUNE))
                .arguments(
                        new RuneCommandElement(Text.of("rune"), rwService)
                )
                .executor(new GiveRuneToPlayerExecutor())
                .build();

        CommandSpec itemAddRarity = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_RARITY))
                .arguments(
                        GenericArguments.integer(Text.of("level"))
                )
                .executor(new ItemAddRarityExecutor())
                .build();

        CommandSpec itemAddMeta = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_RARITY))
                .arguments(
                        GenericArguments.text(Text.of("meta"), TextSerializers.FORMATTING_CODE, true)
                )
                .executor(new ItemAddMetaExecutor())
                .build();

        CommandSpec itemAddType = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_ITEM_TYPE))
                .arguments(
                        new CommandElementMapLookup(Text.of("type"),
                                () -> itemService.getItemMetaTypes())
                )
                .executor(new ItemAddTypeExecutor())
                .build();

        CommandSpec itemAddGroupRestriction = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_RARITY))
                .arguments(
                        new ClassDefCommandElement(Text.of("group")),
                        GenericArguments.integer(Text.of("level"))
                )
                .executor(new ItemAddGroupRestrictionExecutor())
                .build();

        CommandSpec itemAddRuneword = CommandSpec.builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_RUNEWORD))
                .arguments(
                        new RuneCommandElement(Text.of("rw"), rwService)
                )
                .executor(new ItemAddRunewordExecutor())
                .build();

        CommandSpec itemRoot = CommandSpec
                .builder()
                .description(translate(LocalizationKeys.COMMAND_ADMIN_DESC))
                .permission("ntrpg.item")
                .child(itemEnchant, "enchant", "e")
                .child(itemAddSocket, "socket", "sk")
                .child(itemAddRune, "rune", "r")
                .child(itemAddRuneword, "runeword", "rw")
                .child(itemAddRarity, "rarity", "rrty")
                .child(itemAddMeta, "itemmeta", "imeta", "imt")
                .child(itemAddGroupRestriction, "grouprequirements", "gr")
                .child(itemAddType, "itemType", "it", "type")
                .build();

        Sponge.getCommandManager().register(plugin, itemRoot, "nitem", "item");
    }

    public void registerCommand(CommandBase commandCallable) {
        try {
            Sponge.getCommandManager().register(plugin, commandCallable, commandCallable.getAliases());
        } catch (NoSuchMethodError e) {
            try {
                Object o = Sponge.class.getDeclaredMethod("getCommandDispatcher").invoke(null);
                o.getClass().getDeclaredMethod("register", Object.class, CommandCallable.class, List.class)
                        .invoke(o, plugin, commandCallable, commandCallable.getAliases());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }
    }
}
