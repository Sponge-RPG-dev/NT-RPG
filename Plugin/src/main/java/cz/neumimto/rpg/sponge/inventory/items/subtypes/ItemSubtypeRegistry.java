package cz.neumimto.rpg.sponge.inventory.items.subtypes;

import com.google.common.collect.Maps;
import cz.neumimto.rpg.common.inventory.items.subtypes.ItemSubtype;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ItemSubtypeRegistry implements AdditionalCatalogRegistryModule<ItemSubtypeWrapper> {

    @RegisterCatalog(ItemSubtype.class)
    private final Map<String, ItemSubtypeWrapper> types = Maps.newHashMap();


    @Override
    public void registerAdditionalCatalog(ItemSubtypeWrapper extraCatalog) {
        types.put(extraCatalog.getId(),extraCatalog);
    }

    @Override
    public Optional<ItemSubtypeWrapper> getById(String id) {
        return Optional.ofNullable(types.get(id.toLowerCase()));
    }


    @Override
    public Collection<ItemSubtypeWrapper> getAll() {
        return types.values();
    }
}
