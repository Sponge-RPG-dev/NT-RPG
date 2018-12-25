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
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by NeumimTo on 28.1.2018.
 */
public class ItemMetaHeader extends AbstractSingleData<Text, ItemMetaHeader, ItemMetaHeader.Immutable> {

	public ItemMetaHeader() {
		this(Text.EMPTY);
	}

	public ItemMetaHeader(Text rarity) {
		super(rarity, NKeys.ITEM_META_HEADER);
		registerGettersAndSetters();
	}

	@Override
	public Optional<ItemMetaHeader> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<ItemMetaHeader> a = dataHolder.get(ItemMetaHeader.class);
		if (a.isPresent()) {
			ItemMetaHeader otherData = a.get();
			ItemMetaHeader finalData = overlap.merge(this, otherData);
			setValue(finalData.getValue());
		}
		return Optional.of(this);
	}


	@Override
	public Optional<ItemMetaHeader> from(DataContainer container) {
		return from((DataView) container);
	}

	public Optional<ItemMetaHeader> from(DataView view) {
		if (view.contains(NKeys.ITEM_META_HEADER.getQuery())) {
			setValue((Text) view.get(NKeys.ITEM_META_HEADER.getQuery()).get());
			return Optional.of(this);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public ItemMetaHeader copy() {
		return new ItemMetaHeader(getValue());
	}

	@Override
	protected Value<Text> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_HEADER, getValue());
	}

	@Override
	public ItemMetaHeader.Immutable asImmutable() {
		return new ItemMetaHeader.Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return ItemMetaHeader.Builder.CONTENT_VERSION;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer dataContainer = super.toContainer();
		dataContainer.set(NKeys.ITEM_META_HEADER, getValue());
		return dataContainer;
	}

	public static class Builder extends AbstractDataBuilder<ItemMetaHeader> implements DataManipulatorBuilder<ItemMetaHeader, Immutable> {

		public static final int CONTENT_VERSION = 1;

		public Builder() {
			super(ItemMetaHeader.class, CONTENT_VERSION);
		}

		@Override
		public ItemMetaHeader create() {
			return new ItemMetaHeader();
		}

		@Override
		public Optional<ItemMetaHeader> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		protected Optional<ItemMetaHeader> buildContent(DataView container) throws InvalidDataException {
			return create().from(container);
		}
	}

	public class Immutable extends AbstractImmutableSingleData<Text, Immutable, ItemMetaHeader> {


		public Immutable(Text rarity) {
			super(rarity, NKeys.ITEM_META_HEADER);
			registerGetters();
		}

		public Immutable() {
			this(Text.EMPTY);
		}

		@Override
		public int getContentVersion() {
			return ItemMetaHeader.Builder.CONTENT_VERSION;
		}

		@Override
		public DataContainer toContainer() {
			DataContainer dataContainer = super.toContainer();
			dataContainer.set(NKeys.ITEM_META_HEADER, getValue());
			return dataContainer;
		}

		@Override
		protected ImmutableValue<Text> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_HEADER, getValue()).asImmutable();
		}

		@Override
		public ItemMetaHeader asMutable() {
			return new ItemMetaHeader(getValue());
		}
	}

}
