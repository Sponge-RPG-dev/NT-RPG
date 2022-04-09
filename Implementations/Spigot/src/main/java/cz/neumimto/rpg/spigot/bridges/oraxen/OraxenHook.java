package cz.neumimto.rpg.spigot.bridges.oraxen;

import cz.neumimto.rpg.spigot.items.ItemResolver;

import javax.inject.Inject;

public class OraxenHook {

    @Inject
    private ItemResolver itemResolver;

    public void init() {
        itemResolver.addDatabase("oraxen", new OraxenDatabase());
    }
}
