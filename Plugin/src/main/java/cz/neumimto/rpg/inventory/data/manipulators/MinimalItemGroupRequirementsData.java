package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class MinimalItemGroupRequirementsData extends
        AbstractMappedData<String, Integer,MinimalItemGroupRequirementsData, MinimalItemGroupRequirementsData.Immutable> {
    
    public MinimalItemGroupRequirementsData(Map<String, Integer> value) {
        super(value, NKeys.ITEM_PLAYER_ALLOWED_GROUPS);
    }

    @Override
    public Optional<MinimalItemGroupRequirementsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<MinimalItemGroupRequirementsData> otherData_ = dataHolder.get( MinimalItemGroupRequirementsData.class);
        if (otherData_.isPresent()) {
            MinimalItemGroupRequirementsData otherData = otherData_.get();
            MinimalItemGroupRequirementsData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<MinimalItemGroupRequirementsData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<MinimalItemGroupRequirementsData> from(DataView view) {
        if (view.contains(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery())) {
            Map<String, Integer> si = (Map<String, Integer>) view.getMap(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery()).get();
            setValue(si);
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public MinimalItemGroupRequirementsData copy() {
        return new MinimalItemGroupRequirementsData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery(), getValue());
    }

    @Override 
    public Optional<Integer> get(String key) {
        return Optional.of(getValue().get(key));
    }

    @Override
    public Set<String> getMapKeys() {
        return getValue().keySet();
    }

    @Override
    public MinimalItemGroupRequirementsData put(String key, Integer value) {
        getValue().put(key, value);
        return this;
    }

    @Override
    public MinimalItemGroupRequirementsData putAll(Map<? extends String, ? extends Integer> map) {
        getValue().putAll(map);
        return this;
    }

    @Override
    public MinimalItemGroupRequirementsData remove(String key) {
        if (getValue().containsKey(key))
            getValue().remove(key);
        return this;
    }


    public static class Immutable extends AbstractImmutableMappedData<String, Integer, Immutable, MinimalItemGroupRequirementsData> {


        public Immutable(Map<String, Integer> value) {
            super(value, NKeys.ITEM_PLAYER_ALLOWED_GROUPS);
        }

        @Override
        public MinimalItemGroupRequirementsData asMutable() {
            return new MinimalItemGroupRequirementsData(getValue());
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<MinimalItemGroupRequirementsData> implements DataManipulatorBuilder<MinimalItemGroupRequirementsData, Immutable> {
        public Builder() {
            super(MinimalItemGroupRequirementsData.class, 1);
        }

        @Override
        public MinimalItemGroupRequirementsData create() {
            return new MinimalItemGroupRequirementsData(new HashMap<>());
        }

        @Override
        public Optional<MinimalItemGroupRequirementsData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<MinimalItemGroupRequirementsData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}