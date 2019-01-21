package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.ItemLoreSections;
import cz.neumimto.rpg.inventory.LoreSectionDelimiter;
import cz.neumimto.rpg.inventory.data.DataConstants;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.ItemSocketsData;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 20.1.2018.
 */
@Singleton
@ResourceLoader.ListenerClass
public class ItemLoreBuilderService {

	private static TextColor effectName;
	private static TextColor doubleColon;
	private static TextColor value;
	private static TextColor effectSettings;
	private static TextColor groupMinLevelColor;
	private static Text effectSection;
	private static Text rarity;
	private static Text damage;
	private static Text level;
	private static Text sockets;
	private static Text attributes;
	private static Text requirements;
	private static List<ItemLoreSections> loreOrder;
	private static Map<Integer, Text> rarityMap = new HashMap<>();
	private static Text unknownRarity;
	private static Text metaType;

	public static ItemLoreBuilder create(ItemStack is, List<Text> t) {
		return new ItemLoreBuilder(is, t);
	}

	@Listener
	public void pluginInit(GameStartedServerEvent e) {
		setupColor();
	}

	@Reload(on = ReloadService.PLUGIN_CONFIG)
	public void setupColor() {
		effectName = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_NAME_COLOR).get();
		doubleColon = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_COLON_COLOR).get();
		value = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_VALUE_COLOR).get();
		effectSettings = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_SECTION_COLOR).get();
		groupMinLevelColor = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_GROUP_MIN_LEVEL_COLOR).get();


		effectSection = Localizations.ITEM_EFFECTS_SECTION.toText();
		rarity = Localizations.ITEM_RARITY_SECTION.toText();
		damage = Localizations.ITEM_DAMAGE_SECTION.toText();
		level = Localizations.ITEM_LEVEL_SECTION.toText();
		sockets = Localizations.ITEM_SOCKETS_SECTION.toText();
		attributes = Localizations.ITEM_ATTRIBUTES_SECTIO.toText();
		requirements = Localizations.ITEM_REQUIREMENTS_SECTION.toText();
		metaType = Localizations.ITEM_META_TYPE_NAME.toText();

		loreOrder = pluginConfig.ITEM_LORE_ORDER.stream().map(ItemLoreSections::valueOf).collect(Collectors.toList());

		for (String s : pluginConfig.ITEM_RARITY) {
			String[] split = s.split(",");
			Integer i = Integer.parseInt(split[0]);
			Text t = TextHelper.parse(split[1]);
			rarityMap.put(i, t);
		}

		unknownRarity = Localizations.UNKNOWN_RARITY.toText();

	}

	public static class ItemLoreBuilder {

		private final ItemStack is;
		private final List<Text> t;

		private ItemLoreBuilder(ItemStack is, List<Text> t) {
			this.is = is;
			this.t = t;
		}

		public void createAttributesSection() {
			is.get(NKeys.ITEM_ATTRIBUTE_BONUS).ifPresent(a -> {
				if (a.isEmpty()) {
					return;
				}
				createDelimiter(attributes);
				attributeMapToItemLorePart(a);
			});
		}


		public void attributeMapToItemLorePart(Map<String, Integer> a) {
			int k = 0;
			for (Map.Entry<String, Integer> e : a.entrySet()) {
				ICharacterAttribute attribute = NtRpgPlugin.GlobalScope.propertyService.getAttribute(e.getKey());
				String name = attribute.getName();
				int charsToRead = 3;
				if (name.startsWith("&")) {
					charsToRead = 5;
				}
				Text q = null;
				if (name.length() > charsToRead) {
					q = TextHelper.parse(name.substring(0, charsToRead) + ": ");
				} else {
					q = TextHelper.parse(name + ": ");
				}

				t.add(Text.builder()
						.append(q)
						.append(Text.builder(e.getValue() + " ").color(TextColors.WHITE).build())
						.build());
				k++;
				if (k % 3 == 0) {
					t.add(Text.EMPTY);
				}
			}
		}

		public void createItemSocketsSection() {
			is.get(ItemSocketsData.class).ifPresent(a -> {
				List<SocketType> sockets = a.getSockets();
				List<Text> content = a.getContent();
				createDelimiter(ItemLoreBuilderService.sockets);
				for (int i = 0; i < sockets.size(); i++) {
					if (DataConstants.EMPTY_SOCKET.equals(content.get(i))) {
						t.add(Localizations.SOCKET_EMPTY.toText(Arg.arg("socket", sockets.get(i).getName())));
					} else {
						t.add(Text.builder("- ").color(TextColors.DARK_RED).append(content).build());
					}
				}
			});
		}

		public void createItemMetaSection() {
			is.get(NKeys.ITEM_META_HEADER).ifPresent(a -> {
				createDelimiter(a);
				is.get(NKeys.ITEM_RARITY).ifPresent(r -> {
					t.add(Text.builder().append(rarity).append(translateRarity(r)).append(Text.NEW_LINE).build());
				});
				is.get(NKeys.ITEM_DAMAGE).ifPresent(r -> {
					if (r.max == 0) {
						t.add(Text.builder()
								.append(damage)
								.append(Text.builder(String.valueOf(r.min))
										.color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.min))
										.build())
								.append(Text.NEW_LINE)
								.build());
					} else {
						t.add(Text.builder()
								.append(damage)
								.append(Text.builder(String.valueOf(r.min))
										.color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.min))
										.build())
								.append(Text.builder(" - ").color(TextColors.GRAY).build())
								.append(Text.builder(String.valueOf(r.max))
										.color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.max))
										.build())
								.append(Text.NEW_LINE).build());
					}
				});
				is.get(NKeys.ITEM_META_TYPE).ifPresent(r -> {
					t.add(Text.builder()
							.append(metaType)
							.append(
									Text.builder(r.getName())
											.color(TextColors.GOLD)
											.build()
							)
							.build());
				});
				is.get(NKeys.ITEM_LEVEL).ifPresent(r -> {
					t.add(Text.builder().append(level)
							.append(Text.builder(String.valueOf(r))
									.color(TextColors.YELLOW)
									.build())
							.append(Text.NEW_LINE)
							.build());
				});
			});
		}

		private void createRequirements() {
			Optional<Map<String, Integer>> groups = is.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS);
			Optional<Map<String, Integer>> attr = is.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS);
			if (groups.isPresent() || attr.isPresent()) {
				createDelimiter(requirements);
				groupsToItemLore(groups.orElse(Collections.emptyMap()));
				attributeMapToItemLorePart(attr.orElse(Collections.emptyMap()));
			}
		}

		private void groupsToItemLore(Map<String, Integer> r) {
			for (Map.Entry<String, Integer> stringIntegerEntry : r.entrySet()) {
				Integer value = stringIntegerEntry.getValue();
				String group = stringIntegerEntry.getKey();
				ClassDefinition byName = NtRpgPlugin.GlobalScope.groupService.getClassDefinitionByName(group);
				if (byName != null) {
					if (value > 0) {
						t.add(
								Text.builder("* ")
										.color(TextColors.DARK_RED)
										.style(TextStyles.BOLD)
										.append(Text.builder(byName.getName())
												.color(byName.getPreferedColor())
												.append(Text.builder(": ").color(TextColors.GRAY).style(TextStyles.BOLD).build())
												.append(Text.builder(String.valueOf(value)).color(groupMinLevelColor).build())
												.build())
										.build());
					} else {
						t.add(
								Text.builder("* ")
										.color(TextColors.DARK_RED)
										.style(TextStyles.BOLD)
										.append(Text.builder(byName.getName())
												.color(byName.getPreferedColor())
												.build())
										.build());
					}
				}
			}
		}

		private Text translateRarity(Integer r) {
			Text text = rarityMap.get(r);
			if (text == null) {
				text = unknownRarity;
			}
			return text;
		}

		public void createDelimiter(Text section) {
			LoreSectionDelimiter loreSectionDelimiter = is.get(NKeys.ITEM_SECTION_DELIMITER)
					.orElse(new LoreSectionDelimiter(LoreSectionDelimiter.defaultFirstPart, LoreSectionDelimiter.defaultSecondPart));
			t.add(Text.builder().append(loreSectionDelimiter.firstPart).append(section).append(loreSectionDelimiter.secondPart).build());
		}

		public void createEffectsSection() {
			is.get(NKeys.ITEM_EFFECTS).ifPresent(a -> {
				createDelimiter(effectSection);
				itemEffectsToTextList(a);
			});
		}

		public void itemEffectsToTextList(Map<String, EffectParams> a) {
			for (Map.Entry<String, EffectParams> entry : a.entrySet()) {
				if (entry.getValue() == null) {
					t.add(Text.builder(entry.getKey()).color(effectName).append(Text.NEW_LINE).build());
				} else if (entry.getValue().size() == 1
						&& entry.getKey()
						.equalsIgnoreCase(entry.getValue().entrySet().stream().findFirst().get().getKey())) {
					t.add(Text.builder(entry.getKey()).color(effectName)
							.append(Text.builder(": ").color(doubleColon).build())
							.append(Text.builder(entry.getValue().get(entry.getKey())).color(value).build())
							.build());
				} else {
					t.add(Text.builder(entry.getKey()).color(effectName).build());
					for (Map.Entry<String, String> q : entry.getValue().entrySet()) {
						t.add(Text.builder("  - " + q.getKey()).color(effectSettings)
								.append(Text.builder(": ").color(doubleColon).build())
								.append(Text.builder(q.getValue()).color(value).build())
								.build());
					}
				}
			}
		}


		public List<Text> buildLore() {
			for (ItemLoreSections itemLoreSections : loreOrder) {
				switch (itemLoreSections) {
					case META:
						createItemMetaSection();
						break;
					case EFFECTS:
						createEffectsSection();
						break;
					case SOCKETS:
						createItemSocketsSection();
						break;
					case ATTRIBUTES:
						createAttributesSection();
						break;
					case REQUIREMENTS:
						createRequirements();
						break;
				}
			}
			return t;
		}
	}
}
