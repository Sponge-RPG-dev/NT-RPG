package cz.neumimto.rpg.inventory.slotparsers;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 25.3.2018.
 */
public class SlotIteratorRegistry implements AdditionalCatalogRegistryModule<SlotIterator> {

    @RegisterCatalog(SlotIterator.class)
    private final Map<String, SlotIterator> SlotIterators = Maps.newHashMap();


    @Override
    public void registerAdditionalCatalog(SlotIterator extraCatalog) {
        checkArgument(!SlotIterators.containsKey(extraCatalog.getId()));
        SlotIterators.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<SlotIterator> getById(String id) {
        return Optional.of(SlotIterators.get(id.toLowerCase()));
    }

    @Override
    public Collection<SlotIterator> getAll() {
        return SlotIterators.values();
    }
}
