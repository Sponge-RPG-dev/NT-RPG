package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.items.*;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TestItemService extends AbstractItemService {

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass weapons) {
        return Optional.of(new TestItemType(parsed.itemId, parsed.variant, weapons, parsed.damage, parsed.armor));
    }

    @Override
    public Set<String> getAllItemIds() {
        return null;
    }

    public RpgItemStack getRpgItemStack(RpgItemType type) {
        return new RpgItemStackImpl(type, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }
}
