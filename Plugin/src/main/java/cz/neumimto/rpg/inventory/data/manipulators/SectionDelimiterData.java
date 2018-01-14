package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.LoreSectionDelimiter;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
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
public class SectionDelimiterData extends AbstractSingleData<LoreSectionDelimiter, SectionDelimiterData, SectionDelimiterData.Immutable> {

    public SectionDelimiterData(Text firstPart, Text secondPart) {
        this(new LoreSectionDelimiter(firstPart,secondPart));
    }


    public SectionDelimiterData() {
        this(LoreSectionDelimiter.defaultFirstPart, LoreSectionDelimiter.defaultSecondPart);
    }

    public SectionDelimiterData(LoreSectionDelimiter durability) {
        super(durability, NKeys.ITEM_SECTION_DELIMITER);
    }

    @Override
    public Optional<SectionDelimiterData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<SectionDelimiterData> a = dataHolder.get(SectionDelimiterData.class);
        if (a.isPresent()) {
            SectionDelimiterData otherData = a.get();
            SectionDelimiterData finalData = overlap.merge(this, otherData);
            this.setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<SectionDelimiterData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_SECTION_DELIMITER)) {
            return Optional.empty();
        }

        setValue((LoreSectionDelimiter) container.get(NKeys.ITEM_SECTION_DELIMITER.getQuery()).get());
        return Optional.of(this);
    }

    @Override
    public SectionDelimiterData copy() {
        return new SectionDelimiterData(getValue());
    }

    @Override
    protected Value<LoreSectionDelimiter> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_SECTION_DELIMITER, getValue());
    }

    @Override
    public SectionDelimiterData.Immutable asImmutable() {
        return new SectionDelimiterData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return SectionDelimiterData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_SECTION_DELIMITER, getValue());
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableSingleData<LoreSectionDelimiter, Immutable, SectionDelimiterData> {

        public Immutable(LoreSectionDelimiter durability) {
            super(durability, NKeys.ITEM_SECTION_DELIMITER);
        }


        public Immutable() {
            this(new LoreSectionDelimiter(LoreSectionDelimiter.defaultFirstPart,LoreSectionDelimiter.defaultSecondPart));
        }

        @Override
        public int getContentVersion() {
            return SectionDelimiterData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_SECTION_DELIMITER, getValue());
            return dataContainer;
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_SECTION_DELIMITER, getValue()).asImmutable();
        }

        @Override
        public SectionDelimiterData asMutable() {
            return new SectionDelimiterData(getValue());
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

                SectionDelimiterData data = new SectionDelimiterData();
                LoreSectionDelimiter t = (LoreSectionDelimiter) container.get(NKeys.ITEM_SECTION_DELIMITER.getQuery()).get();
                data.setValue(t);
                container.getSerializable(NKeys.ITEM_SECTION_DELIMITER.getQuery(), SectionDelimiterData.class)
                        .ifPresent(a -> {
                            data.set(NKeys.ITEM_SECTION_DELIMITER, a.getValue());
                        });


                return Optional.of(data);
            }
            return Optional.empty();
        }
    }

}
