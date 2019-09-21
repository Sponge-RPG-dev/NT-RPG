package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.mods.ResultNotificationSkillExecutor;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("skill")
public class SpongeSkillCommands extends BaseCommand  {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    private void playerRunSkillCommand(Player executor, ISkill skill,
                                       @Optional @Flags("action") SkillsCommandFacade.SkillAction action,
                                       @Optional String flagData
    ) {
        IActiveCharacter character = characterService.getCharacter(executor);
        skillsCommandFacade.processSkillAction(character, skill, action, flagData);
    }
}
