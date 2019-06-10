package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;

public class SkillLearnExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<ClassDefinition>getOne(Text.of("class")).ifPresent(aClass -> {
            args.<ISkill>getOne(Text.of("skill")).ifPresent(iSkill -> {
                Player player = (Player) src;
                if (aClass.getSkillTree() != null) {
                    IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
                    Map<String, PlayerClassData> classes = character.getClasses();
                    PlayerClassData playerClassData = classes.get(aClass.getName());
                    aClass.getSkillTreeType().processLearnSkill(character, playerClassData, iSkill);
                } else {
                    String msg = Rpg.get().getLocalizationService().translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
                    player.sendMessage(TextHelper.parse(msg));
                }
            });
        });
        return CommandResult.empty();
    }
}
