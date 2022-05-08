package cz.neumimto.rpg.spigot.bridges.itemsadder;

import cz.neumimto.rpg.spigot.bridges.DatapackManager;

import javax.inject.Inject;

public class ItemsAdderHook {
    @Inject
    private DatapackManager itemResolver;

    public void init() {
        itemResolver.addDatabase("itemsadder", new ItemsAdderDatabase());
    }
}
