package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.sponge.contexts.OnlinePlayer;
import com.sun.org.glassfish.gmbal.Description;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("nadmin|na")
public class SpongeAdminCommands extends BaseCommand {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Inject
    private SpongeCharacterService characterService;

    @Subcommand("effect add")
    @Description("Adds effect, managed by rpg plugin, to the player")
    public void effectAddCommand(Player executor, OnlinePlayer target, IGlobalEffect effect, long duration, @Default("{}") String[] args) {
        String data = String.join("", args);

        IActiveCharacter character = characterService.getCharacter(target.player);

        try {
            adminCommandFacade.commandAddEffectToPlayer(data, effect, duration, character);
        } catch (CommandProcessingException e) {
            executor.sendMessage(Text.of(e.getMessage()));
        }
    }


    @Subcommand("experiences add")
    @Description("Adds N experiences of given source type to a character")
    public void addExperiencesCommand(Player executor, OnlinePlayer target, double amount, @Optional ClassDefinition classDefinition, @Optional String source) {
        ISpongeCharacter character = characterService.getCharacter(target.player);
        try {
            adminCommandFacade.commandAddExperiences(character, amount, classDefinition, source);
        } catch (CommandProcessingException e) {
            executor.sendMessage(Text.of(e.getMessage()));
        }
    }


}