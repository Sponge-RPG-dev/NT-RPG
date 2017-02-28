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
public class InventoryItemMenuData extends AbstractSingleData<String, InventoryItemMenuData, InventoryItemMenuData.Immutable> {
	public InventoryItemMenuData(String s) {
		super(s, NKeys.ANY_STRING);
	}
	@Override
	protected Value<?> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(NKeys.ANY_STRING, getValue());
	}

	@Override
	public Optional<InventoryItemMenuData> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<InventoryItemMenuData> data_ = dataHolder.get(InventoryItemMenuData.class);
		if (data_.isPresent()) {
			InventoryItemMenuData data = data_.get();
			InventoryItemMenuData finalData = overlap.merge(this, data);
			setValue(finalData.getValue());
		}
		return Optional.of(this);
	}

	@Override
	public Optional<InventoryItemMenuData> from(DataContainer container) {
		Optional<String> s = container.getString(NKeys.ANY_STRING.getQuery());
		if (s.isPresent()) {
			setValue(s.get());
			return Optional.of(this);
		}
		return Optional.empty();
	}

	@Override
	public InventoryItemMenuData copy() {
		return new InventoryItemMenuData(getValue());
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	public static class Immutable extends AbstractImmutableSingleData<String, Immutable, InventoryItemMenuData> {
		public Immutable(String s) {
			super(s, NKeys.ANY_STRING);
		}

		@Override
		protected ImmutableValue<?> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(NKeys.ANY_STRING, getValue()).asImmutable();
		}

		@Override
		public InventoryItemMenuData asMutable() {
			return new InventoryItemMenuData(getValue());
		}

		@Override
		public int getContentVersion() {
			return 1;
		}
	}

	public static class Builder extends AbstractDataBuilder<InventoryItemMenuData> implements DataManipulatorBuilder<InventoryItemMenuData, Immutable> {
		public Builder() {
			super(InventoryItemMenuData.class, 1);
		}

		@Override
		public InventoryItemMenuData create() {
			return new InventoryItemMenuData("");
		}

		@Override
		public Optional<InventoryItemMenuData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		protected Optional<InventoryItemMenuData> buildContent(DataView container) throws InvalidDataException {
			return create().from(container.getContainer());
		}
	}
}