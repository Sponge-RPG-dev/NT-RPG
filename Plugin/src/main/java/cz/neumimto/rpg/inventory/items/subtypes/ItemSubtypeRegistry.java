package cz.neumimto.rpg.inventory.items.subtypes;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ItemSubtypeRegistry implements AdditionalCatalogRegistryModule<ItemSubtype> {

    @RegisterCatalog(ItemSubtype.class)
    private final Map<String, ItemSubtype> types = Maps.newHashMap();


    @Override
    public void registerAdditionalCatalog(ItemSubtype extraCatalog) {
        types.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<ItemSubtype> getById(String id) {
        return Optional.ofNullable(types.get(id.toLowerCase()));
    }

    @Override
    public Collection<ItemSubtype> getAll() {
        return types.values();
    }
}
