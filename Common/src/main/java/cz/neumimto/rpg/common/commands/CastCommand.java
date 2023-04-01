package cz.neumimto.rpg.common.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.ISkill;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("ncast|nc")
@CommandPermission("ntrpg.player.cast")
public class CastCommand extends BaseCommand {

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    @CommandCompletion("@learnedskill")
    private void playerRunSkillCommand(ActiveCharacter character, ISkill skill) {
        skillsCommandFacade.executeSkill(character, skill);
    }
}
