package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("skill")
public class SpigotSkillCommands extends BaseCommand  {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    private void playerRunSkillCommand(Player executor, ISkill skill,
                                       @Optional SkillsCommandFacade.SkillAction action,
                                       @Optional String flagData
    ) {
        IActiveCharacter character = characterService.getCharacter(executor);
        skillsCommandFacade.processSkillAction(character, skill, action, flagData);
    }
}

