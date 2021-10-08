package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.ISkill;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("skill")
public class SkillCommands extends BaseCommand {

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    private void playerRunSkillCommand(IActiveCharacter character, ISkill skill,
                                       @Optional SkillsCommandFacade.SkillAction action,
                                       @Optional String flagData
    ) {
        skillsCommandFacade.processSkillAction(character, skill, action, flagData);
    }
}

