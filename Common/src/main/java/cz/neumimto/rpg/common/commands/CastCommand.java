package cz.neumimto.rpg.common.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("cast|ncast|nc")
public class CastCommand extends BaseCommand {

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    @CommandCompletion("@learnedskill")
    private void playerRunSkillCommand(IActiveCharacter character , ISkill skill) {
        skillsCommandFacade.executeSkill(character, skill);
    }
}
