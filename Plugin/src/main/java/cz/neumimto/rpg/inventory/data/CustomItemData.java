package cz.neumimto.rpg.inventory.data;

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
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomItemData extends AbstractData<CustomItemData, CustomItemData.Immutable> {

	private int itemLevel;
	private Map<String, Integer> restrictions;
	private Map<String, Float> enchantements;


	public CustomItemData(int itemLevel, Map<String, Integer> restrictions, Map<String, Float> enchantements) {
		this.itemLevel = itemLevel;
		this.restrictions = restrictions;
		this.enchantements = enchantements;
		registerGettersAndSetters();
	}

	public CustomItemData() {
		itemLevel = 0;
		restrictions = new HashMap<>();
		enchantements = new HashMap<>();
		registerGettersAndSetters();
	}

	@Override
	protected void registerGettersAndSetters() {
		registerFieldGetter(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL, () -> this.itemLevel);
		registerFieldGetter(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS, () -> this.restrictions);
		registerFieldGetter(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS, () -> this.enchantements);

		registerKeyValue(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL, this::itemLevel);
		registerKeyValue(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS, this::groupRestricitons);
		registerKeyValue(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS, this::enchantements);
	}

	public Value<Integer> itemLevel() {
		return Sponge.getRegistry().getValueFactory().createValue(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL, itemLevel);
	}

	public MapValue<String, Float> enchantements() {
		return Sponge.getRegistry().getValueFactory().createMapValue(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS, enchantements);
	}

	public MapValue<String, Integer> groupRestricitons() {
		return Sponge.getRegistry().getValueFactory().createMapValue(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS, restrictions);
	}

	@Override
	public Optional<CustomItemData> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<CustomItemData> otherData_ = dataHolder.get(CustomItemData.class);
		if (otherData_.isPresent()) {
			CustomItemData otherData = otherData_.get();
			CustomItemData finalData = overlap.merge(this, otherData);
			this.itemLevel = finalData.itemLevel;
			this.enchantements = finalData.enchantements;
			this.restrictions = finalData.restrictions;
		}
		return Optional.of(this);
	}

	@Override
	public Optional<CustomItemData> from(DataContainer container) {
		return from((DataView) container);
	}

	public Optional<CustomItemData> from(DataView view) {
		if (view.contains(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS.getQuery()) &&
				view.contains(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS.getQuery()) &&
				view.contains(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL.getQuery())) {
			this.itemLevel = view.getObject(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL.getQuery(), Integer.class).get();
			this.restrictions = (Map<String, Integer>) view.getMap(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS.getQuery()).get();
			this.enchantements = (Map<String, Float>) view.getMap(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS.getQuery()).get();
			return Optional.of(this);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public CustomItemData copy() {
		return new CustomItemData(this.itemLevel, this.restrictions, this.enchantements);
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(this.itemLevel, this.restrictions, this.enchantements);
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		return super.toContainer()
				.set(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS.getQuery(), this.restrictions)
				.set(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS.getQuery(), this.enchantements)
				.set(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL.getQuery(), this.itemLevel);
	}
	

	public static class Immutable extends AbstractImmutableData<Immutable, CustomItemData> {
		private int itemLevel;
		private Map<String, Integer> restrictions;
		private Map<String, Float> enchantements;

		public Immutable(int itemLevel, Map<String, Integer> restrictions, Map<String, Float> enchantements) {
			this.itemLevel = itemLevel;
			this.restrictions = restrictions;
			this.enchantements = enchantements;
			registerGetters();
		}

		@Override
		protected void registerGetters() {
			registerFieldGetter(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL, () -> this.itemLevel);
			registerFieldGetter(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS, () -> this.enchantements);
			registerFieldGetter(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS, () -> this.restrictions);

			registerKeyValue(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL, this::itemLevel);
			registerKeyValue(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS, this::enchantements);
			registerKeyValue(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS, this::groupRestricitons);
		}

		public ImmutableValue<Integer> itemLevel() {
			return Sponge.getRegistry().getValueFactory().createValue(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL, itemLevel).asImmutable();
		}

		public ImmutableMapValue<String, Float> enchantements() {
			return Sponge.getRegistry().getValueFactory().createMapValue(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS, enchantements).asImmutable();
		}

		public ImmutableMapValue<String, Integer> groupRestricitons() {
			return Sponge.getRegistry().getValueFactory().createMapValue(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS, restrictions).asImmutable();
		}


		@Override
		public CustomItemData asMutable() {
			return new CustomItemData(itemLevel, restrictions, enchantements);
		}

		@Override
		public int getContentVersion() {
			return 1;
		}

		@Override
		public DataContainer toContainer() {
			return super.toContainer()
					.set(NKeys.CUSTOM_ITEM_DATA_RESTRICTIONS.getQuery(), this.restrictions)
					.set(NKeys.CUSTOM_ITEM_DATA_ENCHANTEMENTS.getQuery(), this.enchantements)
					.set(NKeys.CUSTOM_ITEM_DATA_ITEM_LEVEL.getQuery(), this.itemLevel);
		}
	}

	public static class Builder extends AbstractDataBuilder<CustomItemData> implements DataManipulatorBuilder<CustomItemData, Immutable> {
		public Builder() {
			super(CustomItemData.class, 1);
		}

		@Override
		public CustomItemData create() {
			return new CustomItemData();
		}

		@Override
		public Optional<CustomItemData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		protected Optional<CustomItemData> buildContent(DataView container) throws InvalidDataException {
			return create().from(container);
		}
	}
}