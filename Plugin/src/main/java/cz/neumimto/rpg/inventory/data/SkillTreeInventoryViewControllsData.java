package cz.neumimto.rpg.inventory.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableBooleanData;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractBooleanData;
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
    public SkillTreeInventoryViewControllsData(String s) {
        super(s, NKeys.SKILLTREE_CONTROLLS);
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLTREE_CONTROLLS, getValue());
    }

    @Override
    public Optional<SkillTreeInventoryViewControllsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<SkillTreeInventoryViewControllsData> data_ = dataHolder.get(SkillTreeInventoryViewControllsData.class);
        if (data_.isPresent()) {
            SkillTreeInventoryViewControllsData data = data_.get();
            SkillTreeInventoryViewControllsData finalData = overlap.merge(this, data);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<SkillTreeInventoryViewControllsData> from(DataContainer container) {
        Optional<Object> s = container.get(NKeys.SKILLTREE_CONTROLLS.getQuery());
        if (s.isPresent()) {
            setValue((String) s.get());
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(NKeys.SKILLTREE_CONTROLLS, getValue());
    }

    @Override
    public SkillTreeInventoryViewControllsData copy() {
        return new SkillTreeInventoryViewControllsData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public static class Immutable extends AbstractImmutableSingleData<String, Immutable, SkillTreeInventoryViewControllsData> {
        public Immutable(String s) {
            super(s, NKeys.SKILLTREE_CONTROLLS);
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
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.SKILLTREE_CONTROLLS, getValue());
        }

        @Override
        public int getContentVersion() {
            return 1;
        }
    }

    public static class Builder extends AbstractDataBuilder<SkillTreeInventoryViewControllsData> implements DataManipulatorBuilder<SkillTreeInventoryViewControllsData, Immutable> {
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
            return create().from(container.getContainer());
        }
    }

}