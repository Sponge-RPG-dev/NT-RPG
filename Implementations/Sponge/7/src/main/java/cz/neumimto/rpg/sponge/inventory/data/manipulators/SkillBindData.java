package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.sponge.inventory.data.NKeys;
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

import java.util.Optional;

public class SkillBindData extends AbstractSingleData<String, SkillBindData, SkillBindData.Immutable> {

    public SkillBindData(String value) {
        super(value, NKeys.SKILLBIND);
    }

    @Override
    public Optional<SkillBindData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<SkillBindData> otherData_ = dataHolder.get(SkillBindData.class);
        if (otherData_.isPresent()) {
            SkillBindData otherData = otherData_.get();
            SkillBindData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<SkillBindData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<SkillBindData> from(DataView view) {
        if (view.contains(NKeys.SKILLBIND.getQuery())) {
            setValue(String.valueOf(view.getString(NKeys.SKILLBIND.getQuery()).get()));
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public SkillBindData copy() {
        return new SkillBindData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLBIND, getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new SkillBindData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.SKILLBIND.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<String, Immutable, SkillBindData> {


        public Immutable(String value) {
            super(value, NKeys.SKILLBIND);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLBIND, getValue()).asImmutable();
        }

        @Override
        public SkillBindData asMutable() {
            return new SkillBindData(getValue());
        }

        @Override
        public int getContentVersion() {
            return Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.SKILLBIND.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<SkillBindData>
            implements DataManipulatorBuilder<SkillBindData, Immutable> {

        protected static int CONTENT_VERSION = 1;

        public Builder() {
            super(SkillBindData.class, 1);
        }

        @Override
        public SkillBindData create() {
            return new SkillBindData("");
        }

        @Override
        public Optional<SkillBindData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<SkillBindData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
