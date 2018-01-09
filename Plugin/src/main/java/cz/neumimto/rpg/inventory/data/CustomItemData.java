package cz.neumimto.rpg.inventory.data;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.effects.EffectParams;
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
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomItemData extends AbstractData<CustomItemData, CustomItemData.Immutable> {


    private Map<String, Integer> attributeRequirements;
    private Map<String, Integer> attributeBonus;
    private Map<String, Float> propertyBonus;
    private Map<String, EffectParams> effects;
    private Map<String, String> sockets;
    private Map<String, Integer> allowedGroups;
    private Text rarity;
    private Text type;
    private Integer itemLevel;
    private Pair<Text, Text> sectionDelimiter;
    private Pair<Integer, Integer> durability;
    private Pair<Double, Double> damageMinMax;

	public CustomItemData(Map<String, Integer> attributeRequirements, Map<String, Integer> attributeBonus,
						  Map<String, Float> propertyBonus, Map<String, EffectParams> effects,
						  Map<String, String> sockets, Map<String, Integer> allowedGroups, Text rarity, Text type,
						  Integer itemLevel, Pair<Text, Text> sectionDelimiter, Pair<Integer, Integer> durability,
						  Pair<Double, Double> damageMinMax) {
		this.attributeRequirements = attributeRequirements;
		this.attributeBonus = attributeBonus;
		this.propertyBonus = propertyBonus;
		this.effects = effects;
		this.sockets = sockets;
		this.allowedGroups = allowedGroups;
		this.rarity = rarity;
		this.type = type;
		this.itemLevel = itemLevel;
		this.sectionDelimiter = sectionDelimiter;
		this.durability = durability;
		this.damageMinMax = damageMinMax;
		registerGettersAndSetters();
	}


	public CustomItemData() {
		this(Collections.emptyMap(), Collections.emptyMap(),Collections.emptyMap(), Collections.emptyMap(),Collections.emptyMap(),
				Collections.emptyMap(), Text.EMPTY, Text.EMPTY, 0, new Pair<>(Text.EMPTY, Text.EMPTY), new Pair<>(-1,-1),new Pair<>(0D,0D));
	}

	@Override
	protected void registerGettersAndSetters() {
		registerKeyValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::attributeRequirements);
		registerKeyValue(NKeys.ITEM_ATTRIBUTE_BONUS, this::attributeBonus);
		registerKeyValue(NKeys.ITEM_PROPERTY_BONUS, this::propertyBonus);
		registerKeyValue(NKeys.ITEM_EFFECTS, this::effects);
		registerKeyValue(NKeys.ITEM_SOCKETS, this::sockets);
		registerKeyValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::allowedGroups);
		registerKeyValue(NKeys.ITEM_RARITY, this::rarity);
		registerKeyValue(NKeys.ITEM_TYPE, this::type);
		registerKeyValue(NKeys.ITEM_LEVEL, this::level);
		registerKeyValue(NKeys.ITEM_LORE_DURABILITY, this::durability);
		registerKeyValue(NKeys.ITEM_SECTION_DELIMITER, this::sectionDelimiter);
		registerKeyValue(NKeys.ITEM_DAMAGE, this::damageMinMax);


		registerFieldGetter(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::getAttributeRequirements);
		registerFieldGetter(NKeys.ITEM_ATTRIBUTE_BONUS, this::getAttributeBonus);
		registerFieldGetter(NKeys.ITEM_PROPERTY_BONUS, this::getPropertyBonus);
		registerFieldGetter(NKeys.ITEM_EFFECTS, this::getEffects);
		registerFieldGetter(NKeys.ITEM_SOCKETS, this::getSockets);
		registerFieldGetter(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::getAllowedGroups);
		registerFieldGetter(NKeys.ITEM_RARITY, this::getRarity);
		registerFieldGetter(NKeys.ITEM_TYPE, this::getType);
		registerFieldGetter(NKeys.ITEM_LEVEL, this::getItemLevel);
		registerFieldGetter(NKeys.ITEM_LORE_DURABILITY, this::getDurability);
		registerFieldGetter(NKeys.ITEM_SECTION_DELIMITER, this::getSectionDelimiter);
		registerFieldGetter(NKeys.ITEM_DAMAGE, this::getDamageMinMax);

		registerFieldSetter(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::setAttributeRequirements);
		registerFieldSetter(NKeys.ITEM_ATTRIBUTE_BONUS, this::setAttributeBonus);
		registerFieldSetter(NKeys.ITEM_PROPERTY_BONUS, this::setPropertyBonus);
		registerFieldSetter(NKeys.ITEM_EFFECTS, this::setEffects);
		registerFieldSetter(NKeys.ITEM_SOCKETS, this::setSockets);
		registerFieldSetter(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::setAllowedGroups);
		registerFieldSetter(NKeys.ITEM_RARITY, this::setRarity);
		registerFieldSetter(NKeys.ITEM_TYPE, this::setType);
		registerFieldSetter(NKeys.ITEM_LEVEL, this::setItemLevel);
		registerFieldSetter(NKeys.ITEM_LORE_DURABILITY, this::setDurability);
		registerFieldSetter(NKeys.ITEM_SECTION_DELIMITER, this::setSectionDelimiter);
		registerFieldSetter(NKeys.ITEM_DAMAGE, this::setDamageMinMax);
	}

	public MapValue<String, Integer> attributeRequirements() {
		return Sponge.getRegistry().getValueFactory()
				.createMapValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this.attributeRequirements);
	}

	public MapValue<String, Integer> attributeBonus() {
		return Sponge.getRegistry().getValueFactory()
				.createMapValue(NKeys.ITEM_ATTRIBUTE_BONUS, this.attributeBonus);
	}

	public MapValue<String, Float> propertyBonus() {
		return Sponge.getRegistry().getValueFactory()
				.createMapValue(NKeys.ITEM_PROPERTY_BONUS, this.propertyBonus);
	}

	public MapValue<String, EffectParams> effects() {
		return Sponge.getRegistry().getValueFactory()
				.createMapValue(NKeys.ITEM_EFFECTS, this.effects);
	}

	public MapValue<String, String> sockets() {
		return Sponge.getRegistry().getValueFactory()
				.createMapValue(NKeys.ITEM_SOCKETS, this.sockets);
	}

	public MapValue<String, Integer> allowedGroups() {
		return Sponge.getRegistry().getValueFactory()
				.createMapValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this.allowedGroups);
	}

	public Value<Text> rarity() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_RARITY, this.rarity);
	}

	public Value<Text> type() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_TYPE, this.rarity);
	}

	public Value<Integer> level() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_LEVEL, this.itemLevel);
	}


	public Value<Pair<Integer, Integer>> durability() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_LORE_DURABILITY, this.durability);
	}

	public Value<Pair<Text, Text>> sectionDelimiter() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_SECTION_DELIMITER, this.sectionDelimiter);
	}

	public Value<Pair<Double, Double>> damageMinMax() {
		return Sponge.getRegistry().getValueFactory()
				.createValue(NKeys.ITEM_DAMAGE, this.damageMinMax);
	}

	private Map<String, Integer> getAttributeRequirements() {
		return attributeRequirements;
	}

	private Map<String, Integer> getAttributeBonus() {
		return attributeBonus;
	}

	private Map<String, Float> getPropertyBonus() {
		return propertyBonus;
	}

	private Map<String, EffectParams> getEffects() {
		return effects;
	}

	private Map<String, String> getSockets() {
		return sockets;
	}

	private Map<String, Integer> getAllowedGroups() {
		return allowedGroups;
	}

	private Text getRarity() {
		return rarity;
	}

	private Text getType() {
		return type;
	}

	private Integer getItemLevel() {
		return itemLevel;
	}

	private Pair<Text, Text> getSectionDelimiter() {
		return sectionDelimiter;
	}

	private Pair<Integer, Integer> getDurability() {
		return durability;
	}

	private Pair<Double, Double> getDamageMinMax() {
		return damageMinMax;
	}

	private void setAttributeRequirements(Map<String, Integer> attributeRequirements) {
		this.attributeRequirements = attributeRequirements;
	}

	private void setAttributeBonus(Map<String, Integer> attributeBonus) {
		this.attributeBonus = attributeBonus;
	}

	private void setPropertyBonus(Map<String, Float> propertyBonus) {
		this.propertyBonus = propertyBonus;
	}

	private void setEffects(Map<String, EffectParams> effects) {
		this.effects = effects;
	}

	private void setSockets(Map<String, String> sockets) {
		this.sockets = sockets;
	}

	private void setAllowedGroups(Map<String, Integer> allowedGroups) {
		this.allowedGroups = allowedGroups;
	}

	private void setRarity(Text rarity) {
		this.rarity = rarity;
	}

	private void setType(Text type) {
		this.type = type;
	}

	private void setItemLevel(Integer itemLevel) {
		this.itemLevel = itemLevel;
	}

	private void setSectionDelimiter(Pair<Text, Text> sectionDelimiter) {
		this.sectionDelimiter = sectionDelimiter;
	}

	private void setDurability(Pair<Integer, Integer> durability) {
		this.durability = durability;
	}

	private void setDamageMinMax(Pair<Double, Double> damageMinMax) {
		this.damageMinMax = damageMinMax;
	}

	@Override
	public Optional<CustomItemData> fill(DataHolder dataHolder, MergeFunction overlap) {
		return Optional.empty();
	}

	@Override
	public Optional<CustomItemData> from(DataContainer container) {
		return Optional.empty();
	}

	@Override
	public CustomItemData copy() {
		return new CustomItemData(attributeRequirements, attributeBonus, propertyBonus,
				effects, sockets, allowedGroups, rarity, type, itemLevel, sectionDelimiter, durability, damageMinMax);
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(attributeRequirements, attributeBonus,
				propertyBonus, effects, sockets, allowedGroups,
				rarity, type, itemLevel, sectionDelimiter,
				durability, damageMinMax);
	}

	@Override
	public int getContentVersion() {
		return Builder.CONTENT_VERSION;
	}

	@Override
	public DataContainer toContainer() {
			DataContainer dataContainer = super.toContainer();
			dataContainer.set(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS,attributeRequirements);
			dataContainer.set(NKeys.ITEM_ATTRIBUTE_BONUS,attributeBonus);
			dataContainer.set(NKeys.ITEM_PROPERTY_BONUS,propertyBonus);
			dataContainer.set(NKeys.ITEM_EFFECTS,effects);
			dataContainer.set(NKeys.ITEM_SOCKETS,sockets);
			dataContainer.set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS,allowedGroups);
			dataContainer.set(NKeys.ITEM_SECTION_DELIMITER,sectionDelimiter);
			dataContainer.set(NKeys.ITEM_LORE_DURABILITY,durability);
			dataContainer.set(NKeys.ITEM_DAMAGE,damageMinMax);


			if (rarity != null)
				dataContainer.set(NKeys.ITEM_RARITY,rarity);

			if (type != null)
				dataContainer.set(NKeys.ITEM_TYPE, type);

			if (itemLevel != null)
				dataContainer.set(NKeys.ITEM_LEVEL, itemLevel);
			return dataContainer;
		}

	public class Immutable extends AbstractImmutableData<Immutable, CustomItemData> {

		private Map<String, Integer> attributeRequirements;
		private Map<String, Integer> attributeBonus;
		private Map<String, Float> propertyBonus;
		private Map<String, EffectParams> effects;
		private Map<String, String> sockets;
		private Map<String, Integer> allowedGroups;
		private Pair<Text, Text> sectionDelimiter;
		private Pair<Integer, Integer> durability;
		private Pair<Double, Double> damageMinMax;
		private Text rarity;
		private Text type;
		private Integer itemLevel;

		public Immutable(Map<String, Integer> attributeRequirements, Map<String, Integer> attributeBonus,
							  Map<String, Float> propertyBonus, Map<String, EffectParams> effects,
							  Map<String, String> sockets, Map<String, Integer> allowedGroups, Text rarity, Text type,
							  Integer itemLevel, Pair<Text, Text> sectionDelimiter, Pair<Integer, Integer> durability,
							  Pair<Double, Double> damageMinMax) {
			this.attributeRequirements = attributeRequirements;
			this.attributeBonus = attributeBonus;
			this.propertyBonus = propertyBonus;
			this.effects = effects;
			this.sockets = sockets;
			this.allowedGroups = allowedGroups;
			this.rarity = rarity;
			this.type = type;
			this.itemLevel = itemLevel;
			this.sectionDelimiter = sectionDelimiter;
			this.durability = durability;
			this.damageMinMax = damageMinMax;
			registerGetters();
		}


		public Immutable() {
			this(Collections.emptyMap(), Collections.emptyMap(),Collections.emptyMap(), Collections.emptyMap(),Collections.emptyMap(),
					Collections.emptyMap(), Text.EMPTY, Text.EMPTY, 0, new Pair<>(Text.EMPTY, Text.EMPTY), new Pair<>(0,0),new Pair<>(0D,0D));
		}

		@Override
		protected void registerGetters() {
			registerKeyValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::attributeRequirements);
			registerKeyValue(NKeys.ITEM_ATTRIBUTE_BONUS, this::attributeBonus);
			registerKeyValue(NKeys.ITEM_PROPERTY_BONUS, this::propertyBonus);
			registerKeyValue(NKeys.ITEM_EFFECTS, this::effects);
			registerKeyValue(NKeys.ITEM_SOCKETS, this::sockets);
			registerKeyValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::allowedGroups);
			registerKeyValue(NKeys.ITEM_RARITY, this::rarity);
			registerKeyValue(NKeys.ITEM_TYPE, this::type);
			registerKeyValue(NKeys.ITEM_LEVEL, this::level);
			registerKeyValue(NKeys.ITEM_LORE_DURABILITY, this::durability);
			registerKeyValue(NKeys.ITEM_SECTION_DELIMITER, this::sectionDelimiter);
			registerKeyValue(NKeys.ITEM_DAMAGE, this::damageMinMax);


			registerFieldGetter(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this::getAttributeRequirements);
			registerFieldGetter(NKeys.ITEM_ATTRIBUTE_BONUS, this::getAttributeBonus);
			registerFieldGetter(NKeys.ITEM_PROPERTY_BONUS, this::getPropertyBonus);
			registerFieldGetter(NKeys.ITEM_EFFECTS, this::getEffects);
			registerFieldGetter(NKeys.ITEM_SOCKETS, this::getSockets);
			registerFieldGetter(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this::getAllowedGroups);
			registerFieldGetter(NKeys.ITEM_RARITY, this::getRarity);
			registerFieldGetter(NKeys.ITEM_TYPE, this::getType);
			registerFieldGetter(NKeys.ITEM_LEVEL, this::getItemLevel);
			registerFieldGetter(NKeys.ITEM_LORE_DURABILITY, this::getDurability);
			registerFieldGetter(NKeys.ITEM_SECTION_DELIMITER, this::getSectionDelimiter);
			registerFieldGetter(NKeys.ITEM_DAMAGE, this::getDamageMinMax);
		}

		public ImmutableMapValue<String, Integer> attributeRequirements() {
			return Sponge.getRegistry().getValueFactory()
					.createMapValue(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, this.attributeRequirements)
					.asImmutable();
		}

		public ImmutableMapValue<String, Integer> attributeBonus() {
			return Sponge.getRegistry().getValueFactory()
					.createMapValue(NKeys.ITEM_ATTRIBUTE_BONUS, this.attributeBonus)
					.asImmutable();
		}

		public ImmutableMapValue<String, Float> propertyBonus() {
			return Sponge.getRegistry().getValueFactory()
					.createMapValue(NKeys.ITEM_PROPERTY_BONUS, this.propertyBonus)
					.asImmutable();
		}

		public ImmutableMapValue<String, EffectParams> effects() {
			return Sponge.getRegistry().getValueFactory()
					.createMapValue(NKeys.ITEM_EFFECTS, this.effects)
					.asImmutable();
		}

		public ImmutableMapValue<String, String> sockets() {
			return Sponge.getRegistry().getValueFactory()
					.createMapValue(NKeys.ITEM_SOCKETS, this.sockets).asImmutable();
		}

		public ImmutableMapValue<String, Integer> allowedGroups() {
			return Sponge.getRegistry().getValueFactory()
					.createMapValue(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, this.allowedGroups).asImmutable();
		}

		public ImmutableValue<Text> rarity() {
			return Sponge.getRegistry().getValueFactory()
					.createValue(NKeys.ITEM_RARITY, this.rarity).asImmutable();
		}

		public ImmutableValue<Text> type() {
			return Sponge.getRegistry().getValueFactory()
					.createValue(NKeys.ITEM_TYPE, this.rarity).asImmutable();
		}

		public ImmutableValue<Integer> level() {
			return Sponge.getRegistry().getValueFactory()
					.createValue(NKeys.ITEM_LEVEL, this.itemLevel).asImmutable();
		}


		public ImmutableValue<Pair<Integer, Integer>> durability() {
			return Sponge.getRegistry().getValueFactory()
					.createValue(NKeys.ITEM_LORE_DURABILITY, this.durability).asImmutable();
		}

		public ImmutableValue<Pair<Text, Text>> sectionDelimiter() {
			return Sponge.getRegistry().getValueFactory()
					.createValue(NKeys.ITEM_SECTION_DELIMITER, this.sectionDelimiter).asImmutable();
		}

		public ImmutableValue<Pair<Double, Double>> damageMinMax() {
			return Sponge.getRegistry().getValueFactory()
					.createValue(NKeys.ITEM_DAMAGE, this.damageMinMax).asImmutable();
		}

		private Map<String, Integer> getAttributeRequirements() {
			return attributeRequirements;
		}

		private Map<String, Integer> getAttributeBonus() {
			return attributeBonus;
		}

		private Map<String, Float> getPropertyBonus() {
			return propertyBonus;
		}

		private Map<String, EffectParams> getEffects() {
			return effects;
		}

		private Map<String, String> getSockets() {
			return sockets;
		}

		private Map<String, Integer> getAllowedGroups() {
			return allowedGroups;
		}

		private Text getRarity() {
			return rarity;
		}

		private Text getType() {
			return type;
		}

		private Integer getItemLevel() {
			return itemLevel;
		}

		private Pair<Text, Text> getSectionDelimiter() {
			return sectionDelimiter;
		}

		private Pair<Integer, Integer> getDurability() {
			return durability;
		}

		private Pair<Double, Double> getDamageMinMax() {
			return damageMinMax;
		}

		@Override
		public CustomItemData asMutable() {
			return new CustomItemData(attributeRequirements, attributeBonus, propertyBonus, effects, sockets, allowedGroups, rarity, type, itemLevel, sectionDelimiter, durability, damageMinMax);
		}

		@Override
		public int getContentVersion() {
			return Builder.CONTENT_VERSION;
		}

		@Override
		public DataContainer toContainer() {
			DataContainer dataContainer = super.toContainer();
			dataContainer.set(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS,attributeRequirements);
			dataContainer.set(NKeys.ITEM_ATTRIBUTE_BONUS,attributeBonus);
			dataContainer.set(NKeys.ITEM_PROPERTY_BONUS,propertyBonus);
			dataContainer.set(NKeys.ITEM_EFFECTS,effects);
			dataContainer.set(NKeys.ITEM_SOCKETS,sockets);
			dataContainer.set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS,allowedGroups);
			dataContainer.set(NKeys.ITEM_SECTION_DELIMITER,sectionDelimiter);
			dataContainer.set(NKeys.ITEM_LORE_DURABILITY,durability);
			dataContainer.set(NKeys.ITEM_DAMAGE,damageMinMax);


			if (rarity != null)
				dataContainer.set(NKeys.ITEM_RARITY,rarity);

			if (type != null)
				dataContainer.set(NKeys.ITEM_TYPE, type);

			if (itemLevel != null)
				dataContainer.set(NKeys.ITEM_LEVEL, itemLevel);
			return dataContainer;
		}
	}

	public class Builder extends AbstractDataBuilder<CustomItemData> implements DataManipulatorBuilder<CustomItemData, Immutable> {
		public static final int CONTENT_VERSION = 1;

		public Builder() {
			super(CustomItemData.class, CONTENT_VERSION);
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
		@SuppressWarnings("unchecked")
		protected Optional<CustomItemData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(
					NKeys.ITEM_EFFECTS,
					NKeys.ITEM_ATTRIBUTE_REQUIREMENTS,
					NKeys.ITEM_ATTRIBUTE_BONUS,
					NKeys.ITEM_PROPERTY_BONUS,
					NKeys.ITEM_SOCKETS,
					NKeys.ITEM_PLAYER_ALLOWED_GROUPS,
					NKeys.ITEM_RARITY,
					NKeys.ITEM_TYPE,
					NKeys.ITEM_LEVEL,
					NKeys.ITEM_LORE_DURABILITY,
					NKeys.ITEM_SECTION_DELIMITER,
					NKeys.ITEM_DAMAGE)) {
				Optional<CustomItemData> customItemData = Optional.of(

						new CustomItemData(
								(Map<String, Integer>) container.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS.getQuery()).orElse(new HashMap<>()),
								(Map<String, Integer>) container.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS.getQuery()).orElse(new HashMap<>()),
								(Map<String, Float>) container.get(NKeys.ITEM_ATTRIBUTE_BONUS.getQuery()).orElse(new HashMap<>()),
								(Map<String, EffectParams>) container.get(NKeys.ITEM_EFFECTS.getQuery()).orElse(new HashMap<>()),
								(Map<String, String>) container.get(NKeys.ITEM_SOCKETS.getQuery()).orElse(new HashMap<>()),
								(Map<String, Integer>) container.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS.getQuery()).orElse(new HashMap<>()),
								(Text) container.get(NKeys.ITEM_RARITY.getQuery()).orElse(Text.EMPTY),
								(Text) container.get(NKeys.ITEM_TYPE.getQuery()).orElse(Text.EMPTY),
								(Integer) container.get(NKeys.ITEM_LEVEL.getQuery()).orElse(0),
								(Pair<Text, Text>) container.get(NKeys.ITEM_SECTION_DELIMITER.getQuery()).orElse(new Pair<>(Text.EMPTY, Text.EMPTY)),
								(Pair<Integer, Integer>) container.get(NKeys.ITEM_LORE_DURABILITY.getQuery()).orElse(new Pair<>(-1, -1)),
								(Pair<Double, Double>) container.get(NKeys.ITEM_DAMAGE.getQuery()).orElse(new Pair<>(0, 0))
						)
				);
				return customItemData;
			}
			return Optional.empty();
		}
	}

}