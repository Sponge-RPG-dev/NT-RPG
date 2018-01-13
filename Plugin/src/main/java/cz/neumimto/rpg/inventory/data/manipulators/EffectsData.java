package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 12.1.2018.
 */
public class EffectsData extends AbstractData<EffectsData, EffectsData.Immutable> {
    
    private Map<String, EffectParams> effects;
    

    public EffectsData(Map<String, EffectParams> effects) {
       this.effects = effects;
       registerGettersAndSetters();
    }


    public EffectsData() {
        this(new HashMap<>());
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_EFFECTS, this::effects);

        registerFieldGetter(NKeys.ITEM_EFFECTS, this::getEffects);

        registerFieldSetter(NKeys.ITEM_EFFECTS, this::setEffects);
    }

    public MapValue<String, EffectParams> effects() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(NKeys.ITEM_EFFECTS, this.effects);
    }


    private Map<String, EffectParams> getEffects() {
        return effects;
    }

    private void setEffects(Map<String, EffectParams> effects) {
        this.effects = effects;
    }

    @Override
    public Optional<EffectsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<EffectsData> a = dataHolder.get(EffectsData.class);
        if (a.isPresent()) {
            EffectsData otherData = a.get();
            EffectsData finalData = overlap.merge(this, otherData);
            this.effects = finalData.effects;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<EffectsData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_EFFECTS)) {
            return Optional.empty();
        }

        effects = (Map<String, EffectParams>) container.getMap(NKeys.ITEM_EFFECTS.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public EffectsData copy() {
        return new EffectsData(effects);
    }

    @Override
    public EffectsData.Immutable asImmutable() {
        return new EffectsData.Immutable(effects);
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

    public class Immutable extends AbstractImmutableData<Immutable, EffectsData> {

        private Map<String, EffectParams> effects;


        public Immutable(Map<String, EffectParams> effects) {
            this.effects = effects;
            registerGetters();
        }


        public Immutable() {
            this(Collections.emptyMap());
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_EFFECTS, this::effects);

            registerFieldGetter(NKeys.ITEM_EFFECTS, this::getEffects);
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

    public static class Builder extends AbstractDataBuilder<EffectsData> implements DataManipulatorBuilder<EffectsData, EffectsData.Immutable> {
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
                    effectsData.effects().put(q.getKey(), params);
                }

                container.getSerializable(NKeys.ITEM_EFFECTS.getQuery(), EffectsData.class)
                        .ifPresent(a -> {
                    effectsData.set(NKeys.ITEM_EFFECTS, a.effects);
                });


                return Optional.of(effectsData);
            }
            return Optional.empty();
        }
    }

}