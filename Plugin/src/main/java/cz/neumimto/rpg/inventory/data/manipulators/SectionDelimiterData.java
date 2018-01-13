package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.Pair;
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
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class SectionDelimiterData extends AbstractData<SectionDelimiterData, SectionDelimiterData.Immutable> {

    private Pair<Text, Text> delimiter;

    public SectionDelimiterData() {
        this(new Pair<>(Text.EMPTY, Text.EMPTY));
    }

    public SectionDelimiterData(Pair<Text, Text> delimiter) {
        this.delimiter = delimiter;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_SECTION_DELIMITER, this::delimiter);

        registerFieldGetter(NKeys.ITEM_SECTION_DELIMITER, this::getDelimiter);

        registerFieldSetter(NKeys.ITEM_SECTION_DELIMITER, this::setDelimiter);
    }

    public Value<Pair<Text, Text>> delimiter() {
        return Sponge.getRegistry().getValueFactory()
                .createValue(NKeys.ITEM_SECTION_DELIMITER, this.delimiter);
    }

    public Pair<Text, Text> getDelimiter() {
        return delimiter;
    }

    private void setDelimiter(Pair<Text, Text> delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public Optional<SectionDelimiterData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<SectionDelimiterData> a = dataHolder.get(SectionDelimiterData.class);
        if (a.isPresent()) {
            SectionDelimiterData otherData = a.get();
            SectionDelimiterData finalData = overlap.merge(this, otherData);
            this.delimiter = finalData.delimiter;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<SectionDelimiterData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_SECTION_DELIMITER)) {
            return Optional.empty();
        }

        delimiter = (Pair<Text, Text>) container.get(NKeys.ITEM_SECTION_DELIMITER.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public SectionDelimiterData copy() {
        return new SectionDelimiterData(delimiter);
    }

    @Override
    public SectionDelimiterData.Immutable asImmutable() {
        return new SectionDelimiterData.Immutable(delimiter);
    }

    @Override
    public int getContentVersion() {
        return SectionDelimiterData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_SECTION_DELIMITER, delimiter);
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableData<Immutable, SectionDelimiterData> {

        private Pair<Text, Text> delimiter;


        public Immutable(Pair<Text, Text> delimiter) {
            this.delimiter = delimiter;
            registerGetters();
        }


        public Immutable() {
            this(new Pair<>(Text.EMPTY, Text.EMPTY));
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_SECTION_DELIMITER, this::delimiter);

            registerFieldGetter(NKeys.ITEM_SECTION_DELIMITER, this::getDelimiter);
        }

        public ImmutableValue<Pair<Text, Text>> delimiter() {
            return Sponge.getRegistry().getValueFactory()
                    .createValue(NKeys.ITEM_SECTION_DELIMITER, this.delimiter)
                    .asImmutable();
        }


        private Pair<Text, Text> getDelimiter() {
            return delimiter;
        }

        @Override
        public int getContentVersion() {
            return SectionDelimiterData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_SECTION_DELIMITER, delimiter);
            return dataContainer;
        }

        @Override
        public SectionDelimiterData asMutable() {
            return new SectionDelimiterData(delimiter);
        }
    }

    public static class Builder extends AbstractDataBuilder<SectionDelimiterData> implements DataManipulatorBuilder<SectionDelimiterData, Immutable> {
        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(SectionDelimiterData.class, CONTENT_VERSION);
        }

        @Override
        public SectionDelimiterData create() {
            return new SectionDelimiterData();
        }

        @Override
        public Optional<SectionDelimiterData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Optional<SectionDelimiterData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(NKeys.ITEM_SECTION_DELIMITER)) {
                return Optional.of(
                        new SectionDelimiterData((Pair<Text, Text>) container.get(NKeys.ITEM_SECTION_DELIMITER.getQuery()).orElse(new Pair<>(0D,0D)))
                );
            }
            return Optional.empty();
        }
    }

}

