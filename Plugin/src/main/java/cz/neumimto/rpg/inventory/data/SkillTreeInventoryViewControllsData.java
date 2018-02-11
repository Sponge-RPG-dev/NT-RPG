package cz.neumimto.rpg.inventory.data;

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

/**
 * Created by NeumimTo on 8.10.2017.
 */
public class SkillTreeInventoryViewControllsData extends AbstractSingleData<String, SkillTreeInventoryViewControllsData, SkillTreeInventoryViewControllsData.Immutable> {

    public SkillTreeInventoryViewControllsData(String value) {
        super(value, NKeys.SKILLTREE_CONTROLLS);
    }

    @Override
    public Optional<SkillTreeInventoryViewControllsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<SkillTreeInventoryViewControllsData> otherData_ = dataHolder.get(SkillTreeInventoryViewControllsData.class);
        if (otherData_.isPresent()) {
            SkillTreeInventoryViewControllsData otherData = otherData_.get();
            SkillTreeInventoryViewControllsData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<SkillTreeInventoryViewControllsData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<SkillTreeInventoryViewControllsData> from(DataView view) {
        if (view.contains(NKeys.SKILLTREE_CONTROLLS.getQuery())) {
            setValue(view.getString(NKeys.SKILLTREE_CONTROLLS.getQuery()).get());
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public SkillTreeInventoryViewControllsData copy() {
        return new SkillTreeInventoryViewControllsData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLTREE_CONTROLLS, getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new SkillTreeInventoryViewControllsData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.SKILLTREE_CONTROLLS.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<String, Immutable, SkillTreeInventoryViewControllsData> {


        public Immutable(String value) {
            super(value, NKeys.SKILLTREE_CONTROLLS);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLTREE_CONTROLLS, getValue()).asImmutable();
        }

        @Override
        public SkillTreeInventoryViewControllsData asMutable() {
            return new SkillTreeInventoryViewControllsData(getValue());
        }

        @Override
        public int getContentVersion() {
            return Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.SKILLTREE_CONTROLLS.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<SkillTreeInventoryViewControllsData>
            implements DataManipulatorBuilder<SkillTreeInventoryViewControllsData, Immutable> {
        protected static int CONTENT_VERSION = 1;
        public Builder() {
            super(SkillTreeInventoryViewControllsData.class, 1);
        }

        @Override
        public SkillTreeInventoryViewControllsData create() {
            return new SkillTreeInventoryViewControllsData("");
        }

        @Override
        public Optional<SkillTreeInventoryViewControllsData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<SkillTreeInventoryViewControllsData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
