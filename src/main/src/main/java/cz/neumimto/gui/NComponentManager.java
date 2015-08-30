package cz.neumimto.gui;

import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Listener;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.Component;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.block.BlockBreakEvent;

/**
 * Created by NeumimTo on 8.8.2015.
 */
@Singleton
@Listener
public class NComponentManager implements ComponentManager {

    @Inject
    private Game game;

    private Component root;
    private Component notificationPanel;

    @PostProcess(priority = 100)
    public void initializeComponents() {
    }

    @Subscribe
    public void blockBreakEvent(BlockBreakEvent event) {
    }

    @Override
    public void initPlayerGUI(String playerUUID) {
    }//Event called each time the player load is screen.

    @Override
    public void receiveForm(String playerUUID, Form form) {


    }

    public void sendMessage(String playerUUID, String message) {

    }

}
