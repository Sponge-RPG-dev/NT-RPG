package cz.neumimto.rpg.sponge.inventory.items;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by NeumimTo on 30.3.2018.
 */
public class ItemMetaTypeRegistry implements AdditionalCatalogRegistryModule<ItemMetaType> {

    @RegisterCatalog(ItemMetaType.class)
    private final Map<String, ItemMetaType> SlotIterators = Maps.newHashMap();


    @Override
    public void registerAdditionalCatalog(ItemMetaType extraCatalog) {
        checkArgument(!SlotIterators.containsKey(extraCatalog.getId()));
        SlotIterators.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<ItemMetaType> getById(String id) {
        return Optional.of(SlotIterators.get(id.toLowerCase()));
    }

    @Override
    public Collection<ItemMetaType> getAll() {
        return SlotIterators.values();
    }
}
