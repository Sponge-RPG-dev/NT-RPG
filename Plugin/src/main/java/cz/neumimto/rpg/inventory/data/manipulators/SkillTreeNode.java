package cz.neumimto.rpg.inventory.data.manipulators;

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

import java.util.Optional;

/**
 * Created by NeumimTo on 8.10.2017.
 */
public class SkillTreeNode extends AbstractSingleData<String, SkillTreeNode, SkillTreeNode.Immutable> {

    public SkillTreeNode(String value) {
        super(value, NKeys.SKILLTREE_NODE);
    }

    @Override
    public Optional<SkillTreeNode> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<SkillTreeNode> otherData_ = dataHolder.get(SkillTreeNode.class);
        if (otherData_.isPresent()) {
            SkillTreeNode otherData = otherData_.get();
            SkillTreeNode finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<SkillTreeNode> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<SkillTreeNode> from(DataView view) {
        if (view.contains(NKeys.SKILLTREE_NODE.getQuery())) {
            setValue(String.valueOf(view.getString(NKeys.SKILLTREE_NODE.getQuery()).get()));
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public SkillTreeNode copy() {
        return new SkillTreeNode(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLTREE_NODE, getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new SkillTreeNode.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.SKILLTREE_NODE.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<String, Immutable, SkillTreeNode> {


        public Immutable(String value) {
            super(value, NKeys.SKILLTREE_NODE);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.SKILLTREE_NODE, getValue()).asImmutable();
        }

        @Override
        public SkillTreeNode asMutable() {
            return new SkillTreeNode(getValue());
        }

        @Override
        public int getContentVersion() {
            return Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.SKILLTREE_NODE.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<SkillTreeNode>
            implements DataManipulatorBuilder<SkillTreeNode, Immutable> {

        protected static int CONTENT_VERSION = 1;

        public Builder() {
            super(SkillTreeNode.class, 1);
        }

        @Override
        public SkillTreeNode create() {
            return new SkillTreeNode("");
        }

        @Override
        public Optional<SkillTreeNode> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<SkillTreeNode> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
