package cz.neumimto.rpg.common.configuration;

import com.typesafe.config.Config;
import cz.neumimto.rpg.common.items.AbstractItemService;

public class ItemGroupsLoader {

    private final Config config;
    private final AbstractItemService itemService;

    public ItemGroupsLoader(Config config, AbstractItemService itemService) {
        this.config = config;
        this.itemService = itemService;
        init();
    }

    private void init() {

    }
}
