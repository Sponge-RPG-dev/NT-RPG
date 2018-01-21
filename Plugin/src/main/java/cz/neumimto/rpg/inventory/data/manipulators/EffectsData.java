package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;

import java.util.*;

/**
 * Created by NeumimTo on 12.1.2018.
 * /nadmin enchant add bash {"damage":"10","chance":"1%"}
 */
public class EffectsData extends AbstractMappedData<String, EffectParams, EffectsData, EffectsData.Immutable> {


    public EffectsData() {
        this(new HashMap<>());
    }

    public EffectsData(Map<String, EffectParams> effects) {
        super(effects, NKeys.ITEM_EFFECTS);
    }

    @Override
    public Optional<EffectParams> get(String key) {
        return Optional.of(getValue().get(key));
    }

    @Override
    public Set<String> getMapKeys() {
        return getValue().keySet();
    }

    @Override
    public EffectsData put(String key, EffectParams value) {
        getValue().put(key, value);
        return this;
    }

    @Override
    public EffectsData putAll(Map<? extends String, ? extends EffectParams> map) {
        getValue().putAll(map);
        return this;
    }

    @Override
    public EffectsData remove(String key) {
        if (getValue().containsKey(key))
            getValue().remove(key);
        return this;
    }

    @Override
    public Optional<EffectsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<EffectsData> a = dataHolder.get(EffectsData.class);
        if (a.isPresent()) {
            EffectsData otherData = a.get();
            EffectsData finalData = overlap.merge(this, otherData);
            this.setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<EffectsData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_EFFECTS)) {
            return Optional.empty();
        }

        setValue((Map<String, EffectParams>) container.getMap(NKeys.ITEM_EFFECTS.getQuery()).get());
        return Optional.of(this);
    }

    @Override
    public EffectsData copy() {
        return new EffectsData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }


    public class Immutable extends AbstractImmutableMappedData<String, EffectParams, Immutable, EffectsData> {

        private Map<String, EffectParams> effects;


        public Immutable(Map<String, EffectParams> effects) {
            super(effects, NKeys.ITEM_EFFECTS);
            this.effects = effects;
        }


        public Immutable() {
            this(Collections.emptyMap());
        }


        public ImmutableMapValue<String, EffectParams> effects() {
            return Sponge.getRegistry().getValueFactory()
                    .createMapValue(NKeys.ITEM_EFFECTS, this.effects)
                    .asImmutable();
        }


        private Map<String, EffectParams> getEffects() {
            return effects;
        }

        @Override
        public int getContentVersion() {
            return EffectsData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_EFFECTS,effects);
            return dataContainer;
        }

        @Override
        public EffectsData asMutable() {
            return new EffectsData(effects);
        }
    }

    public static class Builder extends AbstractDataBuilder<EffectsData>
            implements DataManipulatorBuilder<EffectsData, EffectsData.Immutable> {

        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(EffectsData.class, CONTENT_VERSION);
        }

        @Override
        public EffectsData create() {
            return new EffectsData();
        }

        @Override
        public Optional<EffectsData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Optional<EffectsData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(NKeys.ITEM_EFFECTS)) {

                EffectsData effectsData = new EffectsData();
                Map<String, Map> map = (Map<String, Map>) container.getMap(NKeys.ITEM_EFFECTS.getQuery()).get();
                for (Map.Entry<String, Map> q : map.entrySet()) {
                    EffectParams params = new EffectParams();
                    params.putAll(q.getValue());
                    effectsData.getValue().put(q.getKey(), params);
                }

                container.getSerializable(NKeys.ITEM_EFFECTS.getQuery(), EffectsData.class)
                        .ifPresent(a -> {
                    effectsData.set(NKeys.ITEM_EFFECTS, a.getValue());
                });


                return Optional.of(effectsData);
            }
            return Optional.empty();
        }
    }
}