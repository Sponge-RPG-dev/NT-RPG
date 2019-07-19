package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class SkillLearnExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService spongeCharacterService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<ClassDefinition>getOne(Text.of("class")).ifPresent(aClass -> {
            args.<ISkill>getOne(Text.of("skill")).ifPresent(iSkill -> {
                Player player = (Player) src;
                if (aClass.getSkillTree() != null) {
                    ISpongeCharacter character = spongeCharacterService.getCharacter(player);
                    Map<String, PlayerClassData> classes = character.getClasses();
                    PlayerClassData playerClassData = classes.get(aClass.getName());
                    aClass.getSkillTreeType().processLearnSkill(character, playerClassData, iSkill);
                    character.getLastTimeInvokedSkillTreeView().reset();
                } else {
                    String msg = Rpg.get().getLocalizationService().translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
                    player.sendMessage(TextHelper.parse(msg));
                }
            });
        });
        return CommandResult.empty();
    }
}
