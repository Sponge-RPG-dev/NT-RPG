/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillCost;
import cz.neumimto.rpg.skills.SkillItemCost;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 26.7.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public abstract class ActiveSkill extends AbstractSkill implements IActiveSkill {

	@Override
	public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
		ExtendedSkillInfo info = character.getSkillInfo(this);

		if (character.isSilenced() && !getSkillTypes().contains(SkillType.CAN_CAST_WHILE_SILENCED)) {
			character.sendMessage(Localizations.PLAYER_IS_SILENCED);
			skillContext.result(SkillResult.CASTER_SILENCED);
			return;
		}
		skillContext.addExecutor(processItemCost(character, info));
		skillContext.sort();
		skillContext.next(character, info, skillContext);
	}

	protected Set<ActiveSkillPreProcessorWrapper> processItemCost(IActiveCharacter character, ExtendedSkillInfo skillInfo) {
		SkillCost invokeCost = skillInfo.getSkillData().getInvokeCost();
		if (invokeCost == null) {
			return Collections.emptySet();
		}
		Player player = character.getPlayer();
		Inventory query = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
		Map<Inventory, Result> itemsToTake = new HashMap<>();
		int c = 0;
		outer:
		for (SkillItemCost skillItemCost : invokeCost.getItemCost()) {
			ItemType itemType = skillItemCost.getItemType();
			int requiredAmount = skillItemCost.getAmount();
			int i = requiredAmount;
			for (Inventory inventory : query) {
				Optional<ItemStack> peek = inventory.peek();
				if (peek.isPresent()) {
					ItemStack itemStack = peek.get();
					if (itemStack.getType() == itemType) {
						requiredAmount -= itemStack.getQuantity();
						if (requiredAmount > 0) {
							int b = i - requiredAmount;
							itemsToTake.put(inventory, new Result(b, skillItemCost.consumeItems()));
							i = requiredAmount;
						} else {
							itemsToTake.put(inventory, new Result(requiredAmount,skillItemCost.consumeItems()));
							break outer;
						}
					}
				}
			}
		}
		if (c == invokeCost.getItemCost().size()) {
			for (Map.Entry<Inventory, Result> e : itemsToTake.entrySet()) {
				Result result = e.getValue();
				if (result.consume) {
					int amount = result.amount;
					Inventory slot = e.getKey();
					slot.peek().get().setQuantity(amount);
				}
			}
		} else {
			return invokeCost.getInsufficientProcessors();
		}
		return Collections.emptySet();
	}

	public abstract void cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext modifier);


	public SkillContext createSkillExecutorContext(ExtendedSkillInfo esi) {
		return new SkillContext(this, esi);
	}


	private static class Result {
		public int amount;
		public boolean consume;

		private Result(int amount, boolean consume) {
			this.amount = amount;
			this.consume = consume;
		}
	}
}
