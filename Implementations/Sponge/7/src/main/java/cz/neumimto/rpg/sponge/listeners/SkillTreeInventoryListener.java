package cz.neumimto.rpg.sponge.listeners;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.gui.SkillTreeControllsButton;
import cz.neumimto.rpg.sponge.gui.SkillTreeViewModel;
import cz.neumimto.rpg.sponge.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Iterator;

/**
 * Created by NeumimTo on 17.6.2018.
 */
public class SkillTreeInventoryListener {

    private final SpongeCharacterService characterService;

    private final NtRpgPlugin plugin;

    private final SkillService skillService;

    public SkillTreeInventoryListener() {
        characterService = NtRpgPlugin.GlobalScope.characterService;
        plugin = NtRpgPlugin.GlobalScope.plugin;
        skillService = NtRpgPlugin.GlobalScope.skillService;
    }

    public void onOptionSelect(ClickInventoryEvent event, Player player) {
        Iterator<SlotTransaction> iterator = event.getTransactions().iterator();
        if (iterator.hasNext()) {
            SlotTransaction t = iterator.next();
            if (t.getOriginal().get(NKeys.SKILLTREE_CONTROLLS).isPresent()) {
                event.setCancelled(true);
                SkillTreeControllsButton command = t.getOriginal().get(NKeys.SKILLTREE_CONTROLLS).get();
                ISpongeCharacter character = characterService.getCharacter(player);
                SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
                switch (command) {
                    case NORTH:
                        viewModel.getLocation().key -= 1;
                        Sponge.getScheduler().createTaskBuilder()
                                .execute(() -> Gui.moveSkillTreeMenu(character))
                                .submit(plugin);
                        break;
                    case SOUTH:
                        viewModel.getLocation().key += 1;
                        Sponge.getScheduler().createTaskBuilder()
                                .execute(() -> Gui.moveSkillTreeMenu(character))
                                .submit(plugin);
                        break;
                    case WEST:
                        viewModel.getLocation().value += 1;
                        Sponge.getScheduler().createTaskBuilder()
                                .execute(() -> Gui.moveSkillTreeMenu(character))
                                .submit(plugin);
                        break;
                    case EAST:
                        viewModel.getLocation().value -= 1;
                        Sponge.getScheduler().createTaskBuilder()
                                .execute(() -> Gui.moveSkillTreeMenu(character))
                                .submit(plugin);
                        break;
                    case MODE:
                        viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
                        //just redraw
                        Sponge.getScheduler().createTaskBuilder()
                                .execute(() -> Gui.moveSkillTreeMenu(character))
                                .submit(plugin);
                        break;
                    default:
                        String node = t.getOriginal().get(NKeys.SKILLTREE_NODE).get();
                        if (viewModel.getInteractiveMode() == SkillTreeViewModel.InteractiveMode.FAST) {

                            ISkill iSkill = skillService.getById(node).get();

                            ClassDefinition classDefinition = viewModel.getViewedClass();
                            PlayerClassData playerClassData = character.getClasses().get(classDefinition.getName());

                            if (character.getSkill(iSkill.getId()) == null) {
                                classDefinition.getSkillTreeType().processLearnSkill(character, playerClassData, iSkill);
                            } else {
                                classDefinition.getSkillTreeType().processUpgradeSkill(character, playerClassData, iSkill);
                            }
                            //redraw
                            Sponge.getScheduler().createTaskBuilder()
                                    .execute(() -> Gui.moveSkillTreeMenu(character))
                                    .submit(plugin);
                        } else {
                            SkillTree tree = viewModel.getSkillTree();
                            event.setCancelled(true);
                            Sponge.getScheduler().createTaskBuilder()
                                    .execute(() -> Gui.displaySkillDetailsInventoryMenu(character, tree, node))
                                    .submit(plugin);

                        }

                }
            }
        }
    }

}
