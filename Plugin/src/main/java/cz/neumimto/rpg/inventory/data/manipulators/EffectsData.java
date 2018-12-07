package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.bridges.itemizer.ItemizerBean;
import cz.neumimto.rpg.effects.EffectDataBean;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableListData;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractListData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.*;

/**
 * Created by NeumimTo on 12.1.2018.
 * /nadmin enchant add bash {"damage":"10","chance":"1%"}
 */
@ItemizerBean(keyId = "effects")
public class EffectsData extends AbstractListData<EffectDataBean, EffectsData, EffectsData.Immutable> {

	public EffectsData(List<EffectDataBean> value) {
		super(value, NKeys.ITEM_EFFECTS);
	}

	@Override
	public Optional<EffectsData> fill(DataHolder dataHolder, MergeFunction overlap) {
		Optional<EffectsData> otherData_ = dataHolder.get(EffectsData.class);
		if (otherData_.isPresent()) {
			EffectsData otherData = otherData_.get();
			EffectsData finalData = overlap.merge(this, otherData);
			finalData.setValue(otherData.getValue());
		}
		return Optional.of(this);
	}

	@Override
	public Optional<EffectsData> from(DataContainer container) {
		return from((DataView) container);
	}

	public Optional<EffectsData> from(DataView view) {
		if (view.contains(NKeys.ITEM_EFFECTS.getQuery())) {
			List<EffectDataBean> list = (List<EffectDataBean>) view.getList(NKeys.ITEM_EFFECTS.getQuery()).get();
			Map<String, EffectParams> paramsMap = new HashMap<>();
			setValue(list);
			return Optional.of(this);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public EffectsData copy() {
		return new EffectsData(getValue());
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		return super.toContainer()
				.set(NKeys.ITEM_EFFECTS.getQuery(), getValue());
	}

	public static class Immutable extends AbstractImmutableListData<EffectDataBean, Immutable, EffectsData> {


		public Immutable(List<EffectDataBean> value) {
			super(value, NKeys.ITEM_EFFECTS);
		}

		@Override
		public EffectsData asMutable() {
			return new EffectsData(getValue());
		}

		@Override
		public int getContentVersion() {
			return 2;
		}

		@Override
		public DataContainer toContainer() {
			return super.toContainer().set(NKeys.ITEM_EFFECTS.getQuery(), getValue());
		}
	}

	public static class EffectDataBuilder extends AbstractDataBuilder<EffectsData> implements DataManipulatorBuilder<EffectsData, Immutable> {

		public EffectDataBuilder() {
			super(EffectsData.class, 1);
		}

		@Override
		public EffectsData create() {
			return new EffectsData(new ArrayList<>());
		}

		@Override
		public Optional<EffectsData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}

		@Override
		protected Optional<EffectsData> buildContent(DataView container) throws InvalidDataException {
			return create().from(container);
		}
	}
}
