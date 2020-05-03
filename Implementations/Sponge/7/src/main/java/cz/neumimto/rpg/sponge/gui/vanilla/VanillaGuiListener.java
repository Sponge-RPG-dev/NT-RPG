package cz.neumimto.rpg.sponge.gui.vanilla;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.util.Tristate;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

@ResourceLoader.ListenerClass
public class VanillaGuiListener {

    private static final Map<UUID, Map<Integer, Consumer<ISpongeCharacter>>> consumerMap = new HashMap<>();

    @Inject
    SpongeCharacterService characterService;

    public static int putAction(UUID player, Consumer<ISpongeCharacter> action) {
        if (!consumerMap.containsKey(player)) {
            consumerMap.put(player, new HashMap<>());
        }
        Map<Integer, Consumer<ISpongeCharacter>> map = consumerMap.get(player);
        int actionId = map.size();
        map.put(actionId, action);

        return actionId;
    }

    public static void clearActions(UUID player) {
        consumerMap.remove(player);
    }

    @Listener(order = Order.FIRST)
    @Include({
            ClickInventoryEvent.Primary.class,
            ClickInventoryEvent.Secondary.class
    })
    @IsCancelled(Tristate.FALSE)
    public void onClick(ClickInventoryEvent event, @Root Player player) {
        event.getTargetInventory();
        List<SlotTransaction> transactions = event.getTransactions();
        for (SlotTransaction transaction : transactions) {
            Optional<Boolean> menu = transaction.getOriginal().get(NKeys.MENU_INVENTORY);
            menu.ifPresent(m -> event.setCancelled(true));

            Optional<String> s = transaction.getOriginal().get(NKeys.MENU_COMMAND);
            s.ifPresent(command -> Rpg.get().scheduleSyncLater(() -> Sponge.getCommandManager().process(player, command)));
        }
    }

    @Listener(order = Order.FIRST)
    @Include({
            ClickInventoryEvent.Middle.class,
            ClickInventoryEvent.Creative.class,
            ClickInventoryEvent.Drop.class,
            ClickInventoryEvent.Drag.class,
            ClickInventoryEvent.NumberPress.class
    })
    public void onClickOther(ClickInventoryEvent event, @Root Player player) {
        List<SlotTransaction> transactions = event.getTransactions();
        for (SlotTransaction transaction : transactions) {
            Optional<Boolean> menu = transaction.getOriginal().get(NKeys.MENU_INVENTORY);
            menu.ifPresent(m -> event.setCancelled(true));
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        clearActions(event.getTargetEntity().getUniqueId());
    }

}
