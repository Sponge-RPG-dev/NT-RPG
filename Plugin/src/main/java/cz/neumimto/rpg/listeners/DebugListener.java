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
package cz.neumimto.rpg.listeners;

import cz.neumimto.rpg.TextHelper;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 22.12.2015.
 */
public class DebugListener {


	@Listener(order = Order.LAST)
	public void debug(DamageEntityEvent event, @First(typeFilter = EntityDamageSource.class) EntityDamageSource entityDamageSource) {
		Entity targetEntity = event.getTargetEntity();

		Entity source = entityDamageSource.getSource();
		if (source.getType() == EntityTypes.PLAYER) {
			((Player) source).sendMessage(Text.of("[Debug] >> " + event.getFinalDamage()));
		}
		if (targetEntity.getType() == EntityTypes.PLAYER) {
			((Player) targetEntity).sendMessage(Text.of("[Debug] << " + event.getFinalDamage()));
		}
	}

	@Listener(order = Order.LAST)
	public void debugi(DamageEntityEvent event, @First(typeFilter = IndirectEntityDamageSource.class) IndirectEntityDamageSource entityDamageSource) {
		Entity targetEntity = event.getTargetEntity();

		Entity source = entityDamageSource.getIndirectSource();
		if (source.getType() == EntityTypes.PLAYER) {
			((Player) source).sendMessage(Text.of("[Debug] >> " + event.getFinalDamage()));
		}
		if (targetEntity.getType() == EntityTypes.PLAYER) {
			((Player) targetEntity).sendMessage(Text.of("[Debug] << " + event.getFinalDamage()));
		}
	}

	@Listener(order = Order.LAST)
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		event.getTargetEntity().sendMessage(TextHelper.parse("&4-=====================-"));
		event.getTargetEntity().sendMessage(TextHelper.parse("&a  Debug logging Enabled "));
		event.getTargetEntity().sendMessage(TextHelper.parse("&4-=====================-"));
	}

	@Listener(order = Order.FIRST)
	public void onClick(ClickInventoryEvent event, @Root Player player) {
		List<SlotTransaction> transactions = event.getTransactions();
		for (SlotTransaction transaction : transactions) {
			Optional<SlotIndex> inventoryProperty = transaction.getSlot().transform().getInventoryProperty(SlotIndex.class);
			if (!inventoryProperty.isPresent()) {
				inventoryProperty = transaction.getSlot().getInventoryProperty(SlotIndex.class);
			}
			inventoryProperty.ifPresent(slotIndex -> player.sendMessage(TextHelper.parse("[Debug] ID:" + slotIndex.getValue()
					+ ", Container: " + transaction.getSlot().parent().getClass().getName())));
		}
	}
}
