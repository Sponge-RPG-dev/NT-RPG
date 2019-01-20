package cz.neumimto.rpg.listeners;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.gui.SkillTreeControllsButton;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.SkillTreeViewModel;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

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
							//todo
							ClassDefinition classDefinition = character.getPrimaryClass().getClassDefinition();
							SkillTree tree = classDefinition.getSkillTree();
							if (character.getSkill(iSkill.getId()) == null) {
								Text data = characterService.characterLearnskill(character, classDefinition, iSkill);
								player.sendMessage(data);
							} else {
								Text data = characterService.upgradeSkill(character, classDefinition, iSkill);
								player.sendMessage(data);
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
