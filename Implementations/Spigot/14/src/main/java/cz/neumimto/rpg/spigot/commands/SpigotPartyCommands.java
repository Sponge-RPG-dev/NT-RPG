package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.commands.PartyCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SpigotPartyCommands extends BaseCommand {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private PartyCommandFacade partyCommandFacade;

    @Inject
    private PartyService partyService;

    @Subcommand("accept")
    @CommandPermission("ntrpg.party.accept")
    public void acceptPartyInviteCommand(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        partyCommandFacade.acceptPartyInvite(character);
    }

    @Subcommand("invite")
    @CommandPermission("ntrpg.party.invite")
    public void inviteToPartyCommand(Player executor, @Flags("target") OnlinePlayer target) {
        IActiveCharacter character = characterService.getCharacter(executor);
        partyService.sendPartyInvite(character.getParty(), characterService.getCharacter(target.player));
    }

    @Subcommand("create")
    @CommandPermission("ntrpg.party.create")
    public void createPartyCommand(Player executor) {
        IActiveCharacter character = characterService.getCharacter(executor);
        partyCommandFacade.createParty(character);
    }

    @Subcommand("kick")
    @CommandPermission("ntrpg.party.kick")
    public void kickFromPartyCommand(Player executor, @Flags("target") OnlinePlayer target) {
        IActiveCharacter character = characterService.getCharacter(executor);
        IActiveCharacter character2 = characterService.getCharacter(target.player);
        partyService.kickCharacterFromParty(character.getParty(), character2);
    }
}
