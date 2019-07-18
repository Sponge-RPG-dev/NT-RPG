package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.sponge.inventory.data.NKeys;
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
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemAttributesData extends AbstractData<ItemAttributesData, ItemAttributesData.Immutable> {

    private Map<String, Integer> attributeBonus;
    private Map<String, Float> propertyBonus;


    public ItemAttributesData(Map<String, Integer> attributeBonus,
                              Map<String, Float> propertyBonus) {
        this.attributeBonus = attributeBonus;
        this.propertyBonus = propertyBonus;
        registerGettersAndSetters();
    }


    public ItemAttributesData() {
        this(Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_ATTRIBUTE_BONUS, this::attributeBonus);
        registerKeyValue(NKeys.ITEM_PROPERTY_BONUS, this::propertyBonus);

        registerFieldGetter(NKeys.ITEM_ATTRIBUTE_BONUS, this::getAttributeBonus);
        registerFieldGetter(NKeys.ITEM_PROPERTY_BONUS, this::getPropertyBonus);

        registerFieldSetter(NKeys.ITEM_ATTRIBUTE_BONUS, this::setAttributeBonus);
        registerFieldSetter(NKeys.ITEM_PROPERTY_BONUS, this::setPropertyBonus);
    }

    public MapValue<String, Integer> attributeBonus() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(NKeys.ITEM_ATTRIBUTE_BONUS, this.attributeBonus);
    }

    public MapValue<String, Float> propertyBonus() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(NKeys.ITEM_PROPERTY_BONUS, this.propertyBonus);
    }


    private Map<String, Integer> getAttributeBonus() {
        return attributeBonus;
    }

    private void setAttributeBonus(Map<String, Integer> attributeBonus) {
        this.attributeBonus = attributeBonus;
    }

    private Map<String, Float> getPropertyBonus() {
        return propertyBonus;
    }

    private void setPropertyBonus(Map<String, Float> propertyBonus) {
        this.propertyBonus = propertyBonus;
    }


    @Override
    public Optional<ItemAttributesData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemAttributesData> a = dataHolder.get(ItemAttributesData.class);
        if (a.isPresent()) {
            ItemAttributesData otherData = a.get();
            ItemAttributesData finalData = overlap.merge(this, otherData);
            this.attributeBonus = finalData.attributeBonus;
            this.propertyBonus = finalData.propertyBonus;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemAttributesData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS) && !container.contains(NKeys.ITEM_PLAYER_ALLOWED_GROUPS)) {
            return Optional.empty();
        }
        this.attributeBonus = (Map<String, Integer>) container.getMap(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS.getQuery()).get();
        this.propertyBonus = (Map<String, Float>) container.getMap(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public ItemAttributesData copy() {
        return new ItemAttributesData(attributeBonus, propertyBonus);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(attributeBonus, propertyBonus);
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_ATTRIBUTE_BONUS, attributeBonus);
        dataContainer.set(NKeys.ITEM_PROPERTY_BONUS, propertyBonus);
        return dataContainer;
    }

    public static class Builder extends AbstractDataBuilder<ItemAttributesData>
            implements DataManipulatorBuilder<ItemAttributesData, ItemAttributesData.Immutable> {

        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemAttributesData.class, CONTENT_VERSION);
        }

        @Override
        public ItemAttributesData create() {
            return new ItemAttributesData();
        }

        @Override
        public Optional<ItemAttributesData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Optional<ItemAttributesData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(
                    NKeys.ITEM_ATTRIBUTE_BONUS,
                    NKeys.ITEM_PROPERTY_BONUS)) {
                return Optional.of(
                        new ItemAttributesData(
                                (Map<String, Integer>) container.get(NKeys.ITEM_ATTRIBUTE_BONUS.getQuery()).orElse(new HashMap<>()),
                                (Map<String, Float>) container.get(NKeys.ITEM_ATTRIBUTE_BONUS.getQuery()).orElse(new HashMap<>())
                        )
                );
            }
            return Optional.empty();
        }
    }

    public class Immutable extends AbstractImmutableData<ItemAttributesData.Immutable, ItemAttributesData> {

        private Map<String, Integer> attributeBonus;
        private Map<String, Float> propertyBonus;

        public Immutable(Map<String, Integer> attributeBonus,
                         Map<String, Float> propertyBonus) {
            this.attributeBonus = attributeBonus;
            this.propertyBonus = propertyBonus;
            registerGetters();
        }


        public Immutable() {
            this(Collections.emptyMap(), Collections.emptyMap());
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_ATTRIBUTE_BONUS, this::attributeBonus);
            registerKeyValue(NKeys.ITEM_PROPERTY_BONUS, this::propertyBonus);


            registerFieldGetter(NKeys.ITEM_ATTRIBUTE_BONUS, this::getAttributeBonus);
            registerFieldGetter(NKeys.ITEM_PROPERTY_BONUS, this::getPropertyBonus);
        }

        public ImmutableMapValue<String, Integer> attributeBonus() {
            return Sponge.getRegistry().getValueFactory()
                    .createMapValue(NKeys.ITEM_ATTRIBUTE_BONUS, this.attributeBonus)
                    .asImmutable();
        }

        public ImmutableMapValue<String, Float> propertyBonus() {
            return Sponge.getRegistry().getValueFactory()
                    .createMapValue(NKeys.ITEM_PROPERTY_BONUS, this.propertyBonus)
                    .asImmutable();
        }


        private Map<String, Integer> getAttributeBonus() {
            return attributeBonus;
        }

        private Map<String, Float> getPropertyBonus() {
            return propertyBonus;
        }

        @Override
        public ItemAttributesData asMutable() {
            return new ItemAttributesData(attributeBonus, propertyBonus);
        }

        @Override
        public int getContentVersion() {
            return ItemAttributesData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_ATTRIBUTE_BONUS, attributeBonus);
            dataContainer.set(NKeys.ITEM_PROPERTY_BONUS, propertyBonus);
            return dataContainer;
        }
    }
}
