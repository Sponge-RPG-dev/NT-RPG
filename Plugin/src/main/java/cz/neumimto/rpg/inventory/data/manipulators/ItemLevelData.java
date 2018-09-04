package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractIntData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.immutable.common.AbstractImmutableIntData;

import java.util.Optional;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemLevelData extends AbstractIntData<ItemLevelData, ItemLevelData.Immutable> {


	public ItemLevelData(int value) {
		super(value, NKeys.ITEM_LEVEL);
	}

	@Override
	protected Value<Integer> getValueGetter() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_LEVEL, this.getValue());
	}


	@Override
	public Optional<ItemLevelData> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<ItemLevelData> a = dataHolder.get(ItemLevelData.class);
		if (a.isPresent()) {
			ItemLevelData otherData = a.get();
			ItemLevelData finalData = overlap.merge(this, otherData);
			setValue(finalData.getValue());
		}
		return Optional.of(this);
	}

	@Override
	public Optional<ItemLevelData> from(DataContainer container) {
		if (!container.contains(NKeys.ITEM_LEVEL)) {
			return Optional.empty();
		}

		setValue((Integer) container.get(NKeys.ITEM_LEVEL.getQuery()).get());
		return Optional.of(this);
	}

	@Override
	public ItemLevelData copy() {
		return new ItemLevelData(getValue());
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	public static class Builder implements DataManipulatorBuilder<ItemLevelData, Immutable> {

		public static final int CONTENT_VERSION = 1;

		public Builder() {
			super();
		}

		@Override
		public ItemLevelData create() {
			return new ItemLevelData(0);
		}

		@Override
		public Optional<ItemLevelData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Optional<ItemLevelData> build(DataView container) throws InvalidDataException {
			if (container.contains(NKeys.ITEM_RARITY)) {
				return Optional.of(
						new ItemLevelData((Integer) container.get(NKeys.ITEM_LEVEL.getQuery()).orElse(new Pair<>(0D, 0D)))
				);
			}
			return Optional.empty();
		}
	}

	public class Immutable extends AbstractImmutableIntData<Immutable, ItemLevelData> {

		public Immutable(int value) {
			super(Immutable.class, value, NKeys.ITEM_LEVEL, ItemLevelData.class, 0, Integer.MAX_VALUE, 0);
		}

		public Immutable() {
			this(0);
		}
	}
}
