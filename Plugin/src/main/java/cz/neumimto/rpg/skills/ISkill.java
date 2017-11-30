/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.gui.GuiHelper;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ISkill extends IEffectSourceProvider {

	String getName();

	void setName(String name);

	void init();

	void skillLearn(IActiveCharacter character);

	void skillUpgrade(IActiveCharacter character, int level);

	void skillRefund(IActiveCharacter character);

	SkillSettings getDefaultSkillSettings();

	void onCharacterInit(IActiveCharacter c, int level);

	SkillResult onPreUse(IActiveCharacter character);

	Set<ISkillType> getSkillTypes();

	SkillSettings getSettings();

	void setSettings(SkillSettings settings);

	String getDescription();

	void setDescription(String description);

	String getLore();

	boolean showsToPlayers();

	SkillItemIcon getIcon();

	String getIconURL();

	void setIconURL(String url);

	DamageType getDamageType();

	void setDamageType(DamageType type);

	default float getFloatNodeValue(ExtendedSkillInfo extendedSkillInfo, ISkillNode node) {
		return getFloatNodeValue(extendedSkillInfo, node.value());
	}

	default float getFloatNodeValue(ExtendedSkillInfo extendedSkillInfo, String node) {
		return extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(node, extendedSkillInfo.getTotalLevel());
	}

	default int getIntNodeValue(ExtendedSkillInfo extendedSkillInfo, ISkillNode node) {
		return getIntNodeValue(extendedSkillInfo, node.value());
	}

	default int getIntNodeValue(ExtendedSkillInfo extendedSkillInfo, String node) {
		return (int) extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(node, extendedSkillInfo.getTotalLevel());
	}

	default long getLongNodeValue(ExtendedSkillInfo extendedSkillInfo, ISkillNode node) {
		return (long) extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(node, extendedSkillInfo.getTotalLevel());
	}

	default double getDoubleNodeValue(ExtendedSkillInfo extendedSkillInfo, String node) {
		return extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(node, extendedSkillInfo.getTotalLevel());
	}

	default double getDoubleNodeValue(ExtendedSkillInfo extendedSkillInfo, ISkillNode node) {
		return getDoubleNodeValue(extendedSkillInfo, node.value());
	}

	default ItemType getItemType() {
		return ItemTypes.STONE;
	}

	@Override
	default IEffectSource getType() {
		return EffectSourceType.SKILL;
	}

	int getId();

	void setId(int runtimeId);

    default <T extends SkillData> T constructSkillData() {
    	return (T) new SkillData(getName());
	}

	default <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {

	}

    default List<ItemStack> configurationToItemStacks(SkillData skillData) {
		List<ItemStack> a = new ArrayList<>();
		if (skillData.getSkillSettings() != null) {
			Map<String, Float> nodes = skillData.getSkillSettings().getNodes();
			for (Map.Entry<String, Float> s : nodes.entrySet()) {
				if (!s.getKey().endsWith("_levelbonus")) {
					String s1 = Utils.configNodeToReadableString(s.getKey());
					Float init = s.getValue();
					Float lbonus = nodes.get(s.getKey() + "_levelbonus");
					ItemStack of = ItemStack.of(ItemTypes.PAPER, 1);
					of.offer(Keys.DISPLAY_NAME, Text.builder(s1).build());
					of.offer(new MenuInventoryData(true));
					of.offer(Keys.ITEM_LORE, Arrays.asList(
							Text.builder(Localization.SKILL_VALUE_STARTS_AT.replaceAll("%1", String.valueOf(init))).build(),
							Text.builder(Localization.SKILL_VALUE_PER_LEVEL.replaceAll("%1", String.valueOf(lbonus))).build()
					));
					a.add(of);
				}
			}
		}
		return a;
	}

	default ItemStack toItemStack(IActiveCharacter character, SkillData skillData) {
		SkillItemIcon icon = getIcon();

		ItemStack is = null;
		if (icon == null || icon.itemType == null) {
			is = GuiHelper.damageTypeToItemStack(getDamageType());
		} else {
			is = icon.toItemStack();
		}
		is.offer(new MenuInventoryData(true));

		List<Text> lore = new ArrayList<>();

		String desc = getDescription();
		String skillTargetType = Localization.SKILL_TYPE_TARGETTED;
		if (this instanceof ActiveSkill) {
			skillTargetType = Localization.SKILL_TYPE_ACTIVE;
		} else if (this instanceof PassiveSkill) {
			skillTargetType = Localization.SKILL_TYPE_PASSIVE;
		}
		if (desc != null) {
			lore.addAll(TextHelper.splitStringByDelimiter(desc));
		}

		lore.add(Text.of(skillTargetType, TextColors.DARK_PURPLE, TextStyles.ITALIC));
		lore.add(Text.EMPTY);

		int minPlayerLevel = skillData.getMinPlayerLevel();
		int maxSkillLevel = skillData.getMaxSkillLevel();
		ExtendedSkillInfo ei = character.getSkill(getName());
		int currentLevel = 0;
		int totalLevel = 0;
		if (ei != null) {
			currentLevel = ei.getLevel();
			totalLevel = ei.getTotalLevel();
		}

		String s = Localization.MIN_PLAYER_LEVEL;
		if (minPlayerLevel > 0) {
			lore.add(Text.builder(s).color(TextColors.YELLOW)
					.append(Text.builder(" " + minPlayerLevel)
							.color(character.getLevel() < minPlayerLevel ? TextColors.RED : TextColors.GREEN)
							.build())
					.build());
		}

		s = Localization.MAX_SKILL_LEVEL + " " + maxSkillLevel;
		lore.add(Text.builder(s)
				.color(TextColors.YELLOW)
				.build());


		lore.add(Text.EMPTY);
		lore.add(Text.builder(Localization.SKILL_LEVEL + " " + currentLevel + " (" + totalLevel + ") ").build());

		if (getLore() != null) {
			String[] split = getLore().split(":n");
			for (String ss : split) {
				lore.add(Text.builder(ss).style(TextStyles.ITALIC).color(TextColors.GOLD).build());
			}
		}

		is.offer(Keys.ITEM_LORE, lore);
		is.offer(Keys.DISPLAY_NAME, Text.builder(getName()).color(character.hasSkill(this.getName()) ? TextColors.GREEN : TextColors.GRAY).style(TextStyles.BOLD).build());
		return is;
	}
}
