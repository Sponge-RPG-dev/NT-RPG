package cz.neumimto.rpg.common.impl;

import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.common.items.TestItemType;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class TestItemService extends AbstractItemService {

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, WeaponClass weapons) {
        return Optional.of(new TestItemType(parsed.itemId, parsed.model, weapons, parsed.damage, parsed.armor));
    }
}