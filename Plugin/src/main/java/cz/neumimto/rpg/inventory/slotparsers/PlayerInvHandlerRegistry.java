package cz.neumimto.rpg.inventory.slotparsers;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by NeumimTo on 25.3.2018.
 */
public class PlayerInvHandlerRegistry implements AdditionalCatalogRegistryModule<PlayerInvHandler> {

    @RegisterCatalog(PlayerInvHandler.class)
    private final Map<String, PlayerInvHandler> SlotIterators = Maps.newHashMap();


    @Override
    public void registerAdditionalCatalog(PlayerInvHandler extraCatalog) {
        checkArgument(!SlotIterators.containsKey(extraCatalog.getId()));
        SlotIterators.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<PlayerInvHandler> getById(String id) {
        return Optional.ofNullable(SlotIterators.get(id.toLowerCase()));
    }

    @Override
    public Collection<PlayerInvHandler> getAll() {
        return SlotIterators.values();
    }
}
