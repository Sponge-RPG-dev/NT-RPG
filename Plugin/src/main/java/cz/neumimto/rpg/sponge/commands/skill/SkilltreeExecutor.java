package cz.neumimto.rpg.sponge.commands.skill;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.gui.SkillTreeViewModel;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SkilltreeExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player p = (Player) src;
            ISpongeCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(p);
            LocalizationService localizationService = NtRpgPlugin.GlobalScope.localizationService;
            if (character.isStub()) {
                String translate = localizationService.translate(LocalizationKeys.CHARACTER_IS_REQUIRED);
                p.sendMessage(TextHelper.parse(translate));
                return CommandResult.empty();
            }
            Optional<ClassDefinition> aClass = args.getOne("class");
            if (!aClass.isPresent()) {
                PlayerClassData primaryClass = character.getPrimaryClass();
                if (primaryClass == null) {
                    String translate = localizationService.translate(LocalizationKeys.NO_PRIMARY_CLASS);
                    p.sendMessage(TextHelper.parse(translate));
                    return CommandResult.builder().build();
                }
                aClass = Optional.of(primaryClass.getClassDefinition());
            }
            ClassDefinition configClass = aClass.get();

            SkillTree skillTree = configClass.getSkillTree();
            for (SkillTreeViewModel treeViewModel : character.getSkillTreeViewLocation().values()) {
                treeViewModel.setCurrent(false);
            }
            SkillTreeViewModel skillTreeViewModel = character.getSkillTreeViewLocation().get(skillTree.getId());
            if (skillTreeViewModel == null) {
                skillTreeViewModel = new SkillTreeViewModel();
                character.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
                skillTreeViewModel.setSkillTree(skillTree);
            } else {
                skillTreeViewModel.setCurrent(true);
            }
            skillTreeViewModel.setViewedClass(configClass);
            Gui.openSkillTreeMenu(character);
            return CommandResult.success();
        } else {
            throw new CommandException(Text.of("Only for players"));
        }
    }
}
