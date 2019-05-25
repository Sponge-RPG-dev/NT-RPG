package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.sponge.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
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
        if (info == PlayerSkillContext.Empty || info == null) {
            src.sendMessage(Localizations.CHARACTER_DOES_NOT_HAVE_SKILL.toText(Arg.arg("skill", skill.getName())));
            //TODO: maybe return?
        }
        NtRpgPlugin.GlobalScope.skillService.executeSkill(character, info, new SkillExecutorCallback() {
            @Override
            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                switch (skillResult.getResult()) {
                    case ON_COOLDOWN:
                        break;
                    case NO_MANA:
                        Gui.sendMessage(character, Localizations.NO_MANA, Arg.EMPTY);
                        break;
                    case NO_HP:
                        Gui.sendMessage(character, Localizations.NO_HP, Arg.EMPTY);
                        break;
                    case CASTER_SILENCED:
                        Gui.sendMessage(character, Localizations.PLAYER_IS_SILENCED, Arg.EMPTY);
                        break;
                    case NO_TARGET:
                        Gui.sendMessage(character, Localizations.NO_TARGET, Arg.EMPTY);
                }
            }
        });
        return CommandResult.success();
    }
}
