package cz.neumimto.rpg.players.attributes;


import com.google.common.collect.Maps;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class AttributeCatalogTypeRegistry implements AdditionalCatalogRegistryModule<Attribute> {

    @RegisterCatalog(ItemMetaType.class)
    private final Map<String, Attribute> SlotIterators = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(Attribute extraCatalog) {
        checkArgument(!SlotIterators.containsKey(extraCatalog.getId()));
        SlotIterators.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<Attribute> getById(String id) {
        return Optional.of(SlotIterators.get(id.toLowerCase()));
    }

    @Override
    public Collection<Attribute> getAll() {
        return SlotIterators.values();
    }
}
