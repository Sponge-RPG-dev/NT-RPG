package cz.neumimto.listeners;

import cz.neumimto.inventory.InventoryService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.ListenerClass;
import cz.neumimto.players.CharacterService;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.inventory.InventoryClickEvent;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@ListenerClass
public class InventoryListener {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

    @org.spongepowered.api.event.Listener
    public void onInventoryClick(InventoryClickEvent event) {

    }


}
