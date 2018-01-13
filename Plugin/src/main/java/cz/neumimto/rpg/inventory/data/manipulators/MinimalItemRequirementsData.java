package cz.neumimto.rpg.inventory.data.manipulators;

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
 * Created by NeumimTo on 13.1.2018.
 */
public class MinimalItemRequirementsData extends AbstractData<MinimalItemRequirementsData, MinimalItemRequirementsData.Immutable> {


    private Map<String, Integer> attributeRequirements;
    private Map<String, Integer> allowedGroups;


    public MinimalItemRequirementsData(Map<String, Integer> attributeRequirements, Map<String, Integer> allowedGroups) {
        this.attributeRequirements = attributeRequirements;
        this.allowedGroups = allowedGroups;
        registerGettersAndSetters();
    }


    public MinimalItemRequirementsData() {
        this(Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::attributeRequirements);
        registerKeyValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::allowedGroups);



        registerFieldGetter(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::getAttributeRequirements);
        registerFieldGetter(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::getAllowedGroups);

        registerFieldSetter(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::setAttributeRequirements);
        registerFieldSetter(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::setAllowedGroups);
    }

    public MapValue<String, Integer> attributeRequirements() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this.attributeRequirements);
    }

    public MapValue<String, Integer> allowedGroups() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this.allowedGroups);
    }


    private Map<String, Integer> getAttributeRequirements() {
        return attributeRequirements;
    }

    private Map<String, Integer> getAllowedGroups() {
        return allowedGroups;
    }

    private void setAttributeRequirements(Map<String, Integer> attributeRequirements) {
        this.attributeRequirements = attributeRequirements;
    }

    private void setAllowedGroups(Map<String, Integer> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    @Override
    public Optional<MinimalItemRequirementsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<MinimalItemRequirementsData> a = dataHolder.get(MinimalItemRequirementsData.class);
        if (a.isPresent()) {
            MinimalItemRequirementsData otherData = a.get();
            MinimalItemRequirementsData finalData = overlap.merge(this, otherData);
            this.attributeRequirements = finalData.attributeRequirements;
            this.allowedGroups = finalData.allowedGroups;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<MinimalItemRequirementsData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_ATTRIBUTE_BONUS) && !container.contains(NKeys.ITEM_PROPERTY_BONUS)) {
            return Optional.empty();
        }
        this.attributeRequirements  = (Map<String, Integer>) container.getMap(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS.getQuery()).get();
        this.allowedGroups  = (Map<String, Integer>) container.getMap(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public MinimalItemRequirementsData copy() {
        return new MinimalItemRequirementsData(attributeRequirements, allowedGroups);
    }

    @Override
    public MinimalItemRequirementsData.Immutable asImmutable() {
        return new MinimalItemRequirementsData.Immutable(attributeRequirements, allowedGroups);
    }

    @Override
    public int getContentVersion() {
        return MinimalItemRequirementsData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS,attributeRequirements);
        dataContainer.set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS,allowedGroups);
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableData<MinimalItemRequirementsData.Immutable, MinimalItemRequirementsData> {

        private Map<String, Integer> attributeRequirements;
        private Map<String, Integer> allowedGroups;


        public Immutable(Map<String, Integer> attributeRequirements, Map<String, Integer> allowedGroups) {
            this.attributeRequirements = attributeRequirements;
            this.allowedGroups = allowedGroups;
            registerGetters();
        }

        public Immutable() {
            this(Collections.emptyMap(), Collections.emptyMap());
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::attributeRequirements);
            registerKeyValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::allowedGroups);

            registerFieldGetter(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::getAttributeRequirements);
            registerFieldGetter(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::getAllowedGroups);
        }

        public ImmutableMapValue<String, Integer> attributeRequirements() {
            return Sponge.getRegistry().getValueFactory()
                    .createMapValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this.attributeRequirements)
                    .asImmutable();
        }


        public ImmutableMapValue<String, Integer> allowedGroups() {
            return Sponge.getRegistry().getValueFactory()
                    .createMapValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this.allowedGroups).asImmutable();
        }

        private Map<String, Integer> getAttributeRequirements() {
            return attributeRequirements;
        }

        private Map<String, Integer> getAllowedGroups() {
            return allowedGroups;
        }

        @Override
        public MinimalItemRequirementsData asMutable() {
            return new MinimalItemRequirementsData(attributeRequirements, allowedGroups);
        }

        @Override
        public int getContentVersion() {
            return MinimalItemRequirementsData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS,attributeRequirements);
            dataContainer.set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS,allowedGroups);
            return dataContainer;
        }
    }

    public static class Builder extends AbstractDataBuilder<MinimalItemRequirementsData> implements DataManipulatorBuilder<MinimalItemRequirementsData, MinimalItemRequirementsData.Immutable> {
        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(MinimalItemRequirementsData.class, CONTENT_VERSION);
        }

        @Override
        public MinimalItemRequirementsData create() {
            return new MinimalItemRequirementsData();
        }

        @Override
        public Optional<MinimalItemRequirementsData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Optional<MinimalItemRequirementsData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(
                    NKeys.ITEM_ATTRIBUTE_REQUIREMENTS,
                    NKeys.ITEM_PLAYER_ALLOWED_GROUPS
            )) {
                return Optional.of(
                        new MinimalItemRequirementsData(
                                (Map<String, Integer>) container.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS.getQuery()).orElse(new HashMap<>()),
                                (Map<String, Integer>) container.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery()).orElse(new HashMap<>())
                        )
                );
            }
            return Optional.empty();
        }
    }
}