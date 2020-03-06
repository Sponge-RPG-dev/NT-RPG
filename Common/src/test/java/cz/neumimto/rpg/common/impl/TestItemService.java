package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.common.items.TestItemType;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Optional;

@Singleton
public class TestItemService extends AbstractItemService {

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass weapons) {
        return Optional.of(new TestItemType(parsed.itemId, parsed.variant, weapons, parsed.damage, parsed.armor));
    }

    public RpgItemStack getRpgItemStack(RpgItemType type) {
        return new RpgItemStackImpl(type, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }
}
