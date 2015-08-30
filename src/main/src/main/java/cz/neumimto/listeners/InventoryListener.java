package cz.neumimto.listeners;

import cz.neumimto.inventory.InventoryService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Listener;
import cz.neumimto.players.CharacterService;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.inventory.InventoryClickEvent;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Listener
public class InventoryListener {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

    @Subscribe
    public void onInventoryClick(InventoryClickEvent event) {

    }




}
