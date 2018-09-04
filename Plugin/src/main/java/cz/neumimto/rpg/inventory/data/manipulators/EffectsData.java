package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 12.1.2018.
 * /nadmin enchant add bash {"damage":"10","chance":"1%"}
 */
public class EffectsData extends AbstractMappedData<String, EffectParams, EffectsData, EffectsData.Immutable> {

	public EffectsData(Map<String, EffectParams> value) {
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
			Map<String, Map> stringMapMap = (Map<String, Map>) view.getMap(NKeys.ITEM_EFFECTS.getQuery()).get();
			Map<String, EffectParams> paramsMap = new HashMap<>();
			stringMapMap.entrySet().stream().forEach(w -> paramsMap.put(w.getKey(), new EffectParams(w.getValue())));
			setValue(paramsMap);
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

	@Override
	public Optional<EffectParams> get(String key) {
		return Optional.of(getValue().get(key));
	}

	@Override
	public Set<String> getMapKeys() {
		return getValue().keySet();
	}

	@Override
	public EffectsData put(String key, EffectParams value) {
		getValue().put(key, value);
		return this;
	}

	@Override
	public EffectsData putAll(Map<? extends String, ? extends EffectParams> map) {
		getValue().putAll(map);
		return this;
	}

	@Override
	public EffectsData remove(String key) {
		if (getValue().containsKey(key)) {
			getValue().remove(key);
		}
		return this;
	}


	public static class Immutable extends AbstractImmutableMappedData<String, EffectParams, Immutable, EffectsData> {


		public Immutable(Map<String, EffectParams> value) {
			super(value, NKeys.ITEM_EFFECTS);
		}

		@Override
		public EffectsData asMutable() {
			return new EffectsData(getValue());
		}

		@Override
		public int getContentVersion() {
			return 1;
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
			return new EffectsData(new HashMap<>());
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
