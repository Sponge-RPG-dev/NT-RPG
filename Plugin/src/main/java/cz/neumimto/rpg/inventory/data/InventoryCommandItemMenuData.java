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
 * Created by NeumiMTo on 13.11.2016.
 */
public class InventoryCommandItemMenuData extends AbstractSingleData<String, InventoryCommandItemMenuData, InventoryCommandItemMenuData.Immutable> {
	public InventoryCommandItemMenuData(String s) {
		super(s, NKeys.COMMAND);
	}
	@Override
	protected Value<?> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(NKeys.COMMAND, getValue());
	}

	@Override
	public Optional<InventoryCommandItemMenuData> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<InventoryCommandItemMenuData> data_ = dataHolder.get(InventoryCommandItemMenuData.class);
		if (data_.isPresent()) {
			InventoryCommandItemMenuData data = data_.get();
			InventoryCommandItemMenuData finalData = overlap.merge(this, data);
			setValue(finalData.getValue());
		}
		return Optional.of(this);
	}

	@Override
	public Optional<InventoryCommandItemMenuData> from(DataContainer container) {
		Optional<String> s = container.getString(NKeys.COMMAND.getQuery());
		if (s.isPresent()) {
			setValue(s.get());
			return Optional.of(this);
		}
		return Optional.empty();
	}

	@Override
	public DataContainer toContainer() {
		return super.toContainer().set(NKeys.COMMAND, getValue());
	}

	@Override
	public InventoryCommandItemMenuData copy() {
		return new InventoryCommandItemMenuData(getValue());
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	public static class Immutable extends AbstractImmutableSingleData<String, Immutable, InventoryCommandItemMenuData> {
		public Immutable(String s) {
			super(s, NKeys.COMMAND);
		}

		@Override
		protected ImmutableValue<?> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(NKeys.COMMAND, getValue()).asImmutable();
		}

		@Override
		public InventoryCommandItemMenuData asMutable() {
			return new InventoryCommandItemMenuData(getValue());
		}

		@Override
		public DataContainer toContainer() {
			return super.toContainer().set(NKeys.COMMAND, getValue());
		}

		@Override
		public int getContentVersion() {
			return 1;
		}
	}

	public static class Builder extends AbstractDataBuilder<InventoryCommandItemMenuData> implements DataManipulatorBuilder<InventoryCommandItemMenuData, Immutable> {
		public Builder() {
			super(InventoryCommandItemMenuData.class, 1);
		}

		@Override
		public InventoryCommandItemMenuData create() {
			return new InventoryCommandItemMenuData("");
		}

		@Override
		public Optional<InventoryCommandItemMenuData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		protected Optional<InventoryCommandItemMenuData> buildContent(DataView container) throws InvalidDataException {
			return create().from(container.getContainer());
		}
	}

}