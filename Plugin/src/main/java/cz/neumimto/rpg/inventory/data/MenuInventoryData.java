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

public class MenuInventoryData extends AbstractSingleData<Boolean, MenuInventoryData, MenuInventoryData.Immutable> {
	public MenuInventoryData() {
		super(true, NKeys.MENU_INVENTORY);
	}
	@Override
	protected Value<?> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(NKeys.MENU_INVENTORY, getValue());
	}

	@Override
	public Optional<MenuInventoryData> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<MenuInventoryData> data_ = dataHolder.get(MenuInventoryData.class);
		if (data_.isPresent()) {
			MenuInventoryData data = data_.get();
			MenuInventoryData finalData = overlap.merge(this, data);
			setValue(finalData.getValue());
		}
		return Optional.of(this);
	}

	@Override
	public Optional<MenuInventoryData> from(DataContainer container) {
		Optional<Boolean> s = container.getBoolean(NKeys.MENU_INVENTORY.getQuery());
		if (s.isPresent()) {
			setValue(s.get());
			return Optional.of(this);
		}
		return Optional.empty();
	}

	@Override
	public MenuInventoryData copy() {
		return new MenuInventoryData();
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable();
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	public static class Immutable extends AbstractImmutableSingleData<Boolean, Immutable, MenuInventoryData> {
		public Immutable() {
			super(true, NKeys.MENU_INVENTORY);
		}

		@Override
		protected ImmutableValue<?> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(NKeys.MENU_INVENTORY, getValue()).asImmutable();
		}

		@Override
		public MenuInventoryData asMutable() {
			return new MenuInventoryData();
		}

		@Override
		public int getContentVersion() {
			return 1;
		}
	}

	public static class Builder extends AbstractDataBuilder<MenuInventoryData> implements DataManipulatorBuilder<MenuInventoryData, Immutable> {
		public Builder() {
			super(MenuInventoryData.class, 1);
		}

		@Override
		public MenuInventoryData create() {
			return new MenuInventoryData();
		}

		@Override
		public Optional<MenuInventoryData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		protected Optional<MenuInventoryData> buildContent(DataView container) throws InvalidDataException {
			return create().from(container.getContainer());
		}
	}
}