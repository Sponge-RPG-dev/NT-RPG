package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class PlayerSkillCastExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);

        if (character == null) return CommandResult.empty(); //Failed

        ISkill skill = args.<ISkill>getOne(Text.of("skill")).get();

        PlayerSkillContext info = character.getSkillInfo(skill.getId());
        final LocalizationService localizationService = Rpg.get().getLocalizationService();
        if (info == PlayerSkillContext.Empty || info == null) {
            src.sendMessage(TextHelper.parse(localizationService.translate(LocalizationKeys.CHARACTER_DOES_NOT_HAVE_SKILL, Arg.arg("skill", skill.getName()))));
            //TODO: maybe return?
        }
        NtRpgPlugin.GlobalScope.skillService.executeSkill(character, info, new SkillExecutorCallback() {
            @Override
            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                switch (skillResult.getResult()) {
                    case ON_COOLDOWN:
                        break;
                    case NO_MANA:
                        character.sendMessage(localizationService.translate(LocalizationKeys.NO_MANA));
                        break;
                    case NO_HP:
                        character.sendMessage(localizationService.translate(LocalizationKeys.NO_HP));
                        break;
                    case CASTER_SILENCED:
                        character.sendMessage(localizationService.translate(LocalizationKeys.PLAYER_IS_SILENCED));
                        break;
                    case NO_TARGET:
                        character.sendMessage(localizationService.translate(LocalizationKeys.NO_TARGET));
                        break;
                }
            }
        });
        return CommandResult.success();
    }
}
