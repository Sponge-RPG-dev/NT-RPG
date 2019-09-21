package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.sponge.contexts.OnlinePlayer;
import com.sun.org.glassfish.gmbal.Description;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.types.IActiveSkill;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;
import cz.neumimto.rpg.sponge.entities.commandblocks.ConsoleSkillExecutor;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

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

    @Subcommand("skill")
    public void adminExecuteSkillCommand(Player executor, ISkill skill, @Flags("level") @Default("1") int level) {
        IActiveCharacter character = characterService.getCharacter(executor);
        if (character.isStub()) {
            throw new RuntimeException("Character is required even for an admin.");
        }
        SkillSettings defaultSkillSettings = skill.getSettings();

        Long l = System.nanoTime();

        PlayerSkillContext playerSkillContext = new PlayerSkillContext(null, skill, character);
        playerSkillContext.setLevel(level);
        SkillData skillData = new SkillData(skill.getId());
        skillData.setSkillSettings(defaultSkillSettings);
        playerSkillContext.setSkillData(skillData);
        playerSkillContext.setSkill(skill);

        SkillContext skillContext = new SkillContext((IActiveSkill) skill, playerSkillContext) {{
            wrappers.add(new SkillExecutorCallback() {
                @Override
                public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                    Long e = System.nanoTime();
                    character.sendMessage("Exec Time: " + TimeUnit.MILLISECONDS.convert(e - l, TimeUnit.NANOSECONDS));
                    if (character instanceof ConsoleSkillExecutor) {
                        Living entity = (Living) character.getEntity();
                        entity.remove();
                    }
                }
            });
        }};
        skillContext.sort();
        skillContext.next(character, playerSkillContext, skillContext);
    }

}