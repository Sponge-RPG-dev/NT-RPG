package cz.neumimto.rpg.spigot.bridges.oraxen;

import cz.neumimto.rpg.spigot.bridges.DatapackManager;

import javax.inject.Inject;

public class OraxenHook {

    @Inject
    private DatapackManager itemResolver;

    public void init() {
        itemResolver.addDatabase("oraxen", new OraxenDatabase());
    }
}
