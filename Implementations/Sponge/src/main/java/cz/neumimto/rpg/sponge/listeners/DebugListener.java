
package cz.neumimto.rpg.sponge.listeners;


import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Created by NeumimTo on 22.12.2015.
 */
@ResourceLoader.ListenerClass
public class DebugListener {

    @Listener(order = Order.LAST)
    public void debug(DamageEntityEvent event, @First(typeFilter = EntityDamageSource.class) EntityDamageSource entityDamageSource) {
        if (Rpg.get().getPluginConfig().DEBUG.isBalance()) {
            Entity targetEntity = event.getTargetEntity();

            Entity source = entityDamageSource.getSource();
            if (source.getType() == EntityTypes.PLAYER) {
                ((Player) source).sendMessage(Text.of("[Debug] >> " + event.getFinalDamage()));
            }
            if (targetEntity.getType() == EntityTypes.PLAYER) {
                ((Player) targetEntity).sendMessage(Text.of("[Debug] << " + event.getFinalDamage()));
            }
        }
    }

    @Listener(order = Order.LAST)
    public void debugi(DamageEntityEvent event, @First(typeFilter = IndirectEntityDamageSource.class) IndirectEntityDamageSource entityDamageSource) {
        if (Rpg.get().getPluginConfig().DEBUG.isBalance()) {
            Entity targetEntity = event.getTargetEntity();

            Entity source = entityDamageSource.getIndirectSource();
            if (source.getType() == EntityTypes.PLAYER) {
                ((Player) source).sendMessage(Text.of("[Debug] >> " + event.getFinalDamage()));
            }
            if (targetEntity.getType() == EntityTypes.PLAYER) {
                ((Player) targetEntity).sendMessage(Text.of("[Debug] << " + event.getFinalDamage()));
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (Rpg.get().getPluginConfig().DEBUG.isBalance()) {
            event.getTargetEntity().sendMessage(TextHelper.parse("&4-=====================-"));
            event.getTargetEntity().sendMessage(TextHelper.parse("&a  Debug logging Enabled "));
            event.getTargetEntity().sendMessage(TextHelper.parse("&4-=====================-"));
        }
    }

    @Listener(order = Order.FIRST)
    @Include({
            ClickInventoryEvent.Primary.class,
            ClickInventoryEvent.Secondary.class
    })
    public void onClick(ClickInventoryEvent event, @Root Player player) {
        if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
            List<SlotTransaction> transactions = event.getTransactions();

            BiConsumer<Player, String> sendMsg = (player1, s) -> player1.sendMessage(TextHelper.parse(s));

            for (SlotTransaction transaction : transactions) {
                Optional<SlotIndex> inventoryProperty = transaction.getSlot().getInventoryProperty(SlotIndex.class);
                Class<? extends Inventory> aClass = transaction.getSlot().parent().getClass();

                player.sendMessage(TextHelper.parse("&4-=====================-"));
                inventoryProperty.ifPresent(slotIndex -> sendMsg.accept(player, "Slot ID: " + slotIndex.getValue()));
                sendMsg.accept(player, "InventoryClass: " + aClass.getCanonicalName());


                Class aClass2 = transaction.getSlot().transform().parent().getClass();

                inventoryProperty = transaction.getSlot().transform().getInventoryProperty(SlotIndex.class);
                inventoryProperty.ifPresent(slotIndex -> sendMsg.accept(player, "Transformed Slot ID: " + slotIndex.getValue()));
                sendMsg.accept(player, "Transformed InventoryClass: " + aClass2.getCanonicalName());

                player.sendMessage(TextHelper.parse("&4-=====================-"));
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void onClick(HandInteractEvent event, @Root Player player) {
        if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
            Hotbar hotbar = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
            int selectedSlotIndex = hotbar.getSelectedSlotIndex();
            BiConsumer<Player, String> sendMsg = (player1, s) -> player1.sendMessage(TextHelper.parse(s));
            player.sendMessage(TextHelper.parse("&4-=====================-"));
            sendMsg.accept(player, "Selected SlotID: " + hotbar.getSelectedSlotIndex());
            sendMsg.accept(player, "InventoryClass: " + hotbar.getClass().getCanonicalName());

            Slot slot = hotbar.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(selectedSlotIndex)));
            Optional<SlotIndex> inventoryProperty = slot.transform().getInventoryProperty(SlotIndex.class);
            sendMsg.accept(player, "Transformed Slot Id: " + inventoryProperty.get().getValue());
        }
    }
}
