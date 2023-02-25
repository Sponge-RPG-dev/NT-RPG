package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.common.items.ItemClass;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.items.TestItemType;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TestItemService extends AbstractItemService {

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass weapons) {
        return Optional.of(new TestItemType(parsed.itemId, parsed.variant, weapons));
    }

    @Override
    public Set<String> getAllItemIds() {
        return null;
    }

}
