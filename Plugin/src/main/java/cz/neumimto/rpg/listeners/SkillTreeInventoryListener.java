package cz.neumimto.rpg.listeners;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.gui.SkillTreeControllsButton;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.players.*;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Iterator;

/**
 * Created by NeumimTo on 17.6.2018.
 */
public class SkillTreeInventoryListener {

	private final CharacterService characterService;

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
				IActiveCharacter character = characterService.getCharacter(player);
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
							if (character.getSkill(iSkill.getId()) == null) {

								ActionResult actionResult = characterService.canLearnSkill(character, classDefinition, iSkill);
								if (actionResult.isOk()) {
									PlayerClassData playerClassData = character.getClasses().get(classDefinition.getName());
									characterService.learnSkill(character, playerClassData, iSkill);
									characterService.putInSaveQueue(character.getCharacterBase());
								} else {
									player.sendMessage(actionResult.getErrorMesage());
								}

							} else {
								ActionResult actionResult = characterService.canUpgradeSkill(character, classDefinition, iSkill);
								if (actionResult.isOk()) {
									PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
									characterService.upgradeSkill(character, skillInfo, iSkill);
									characterService.putInSaveQueue(character.getCharacterBase());
								} else {
									player.sendMessage(actionResult.getErrorMesage());
								}
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
