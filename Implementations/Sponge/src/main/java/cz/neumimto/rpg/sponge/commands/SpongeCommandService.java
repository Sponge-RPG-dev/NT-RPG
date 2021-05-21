

package cz.neumimto.rpg.sponge.commands;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.commands.elements.CommandElementMapLookup;
import cz.neumimto.rpg.sponge.commands.elements.GlobalEffectCommandElement;
import cz.neumimto.rpg.sponge.commands.elements.RuneCommandElement;
import cz.neumimto.rpg.sponge.commands.item.*;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class SpongeCommandService {

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
        registerCharacterCommands();
        registerItemCommands();
    }

    private Text translate(String key) {
        return TextHelper.parse(localizationService.translate(key));
    }


    private void registerCharacterCommands() {


        //==========GROUPS==========

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

}
