package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
@CommandAlias("skilltree")
public class SpigotSkilltreeCommands extends BaseCommand {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    public void openSkillTreeCommand(Player executor, ClassDefinition classDefinition) {
        ISpigotCharacter character = characterService.getCharacter(executor);

        skillsCommandFacade.openSkillTreeCommand(character, classDefinition);
    }
}
