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
import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.IRpgElement;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.gui.GuiHelper;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.parents.PassiveSkill;
import cz.neumimto.rpg.skills.parents.Targeted;
import cz.neumimto.rpg.skills.tree.SkillTree;
import cz.neumimto.rpg.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ISkill extends IEffectSourceProvider, CatalogType, IRpgElement {

	@Override
	String getId();

	Text getLocalizableName();

	void setLocalizableName(Text name);

	void init();

	void skillLearn(IActiveCharacter character);

	void skillUpgrade(IActiveCharacter character, int level);

	void skillRefund(IActiveCharacter character);

	SkillSettings getDefaultSkillSettings();

	void onCharacterInit(IActiveCharacter c, int level);

	void onPreUse(IActiveCharacter character, SkillContext skillContext);

	Set<ISkillType> getSkillTypes();

	SkillSettings getSettings();

	void setSettings(SkillSettings settings);

	List<Text> getDescription();

	void setDescription(List<Text> description);

	List<Text> getLore();

	void setLore(List<Text> lore);

	boolean showsToPlayers();

	SkillItemIcon getIcon();

	void setIcon(ItemType icon);

	String getIconURL();

	void setIconURL(String url);

	DamageType getDamageType();

	void setDamageType(DamageType type);

	default ItemType getItemType() {
		return ItemTypes.STONE;
	}

	@Override
	default IEffectSource getType() {
		return EffectSourceType.SKILL;
	}

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
					ItemStack of = GuiHelper.itemStack(ItemTypes.PAPER);
					of.offer(Keys.DISPLAY_NAME, Text.builder(s1).build());
					of.offer(new MenuInventoryData(true));
					of.offer(Keys.ITEM_LORE, Arrays.asList(
							Text.builder().append(Localizations.SKILL_VALUE_STARTS_AT.toText())
									.style(TextStyles.BOLD)
									.color(TextColors.GOLD)
									.append(Text.builder(": " + init)
											.color(TextColors.GREEN).style(TextStyles.BOLD)
											.build())
									.build()
							,
							Text.builder().append(Localizations.SKILL_VALUE_PER_LEVEL.toText())
									.style(TextStyles.BOLD).color(TextColors.GOLD)
									.append(Text.builder(": " + lbonus)
											.color(TextColors.GREEN).style(TextStyles.BOLD)
											.build())
									.build()
					));
					a.add(of);
				}
			}
		}
		return a;
	}

	default ItemStack toItemStack(IActiveCharacter character, SkillData skillData, SkillTree skillTree) {
		SkillItemIcon icon = getIcon();

		ItemStack is = (icon == null || icon.itemType == null) ? GuiHelper.damageTypeToItemStack(getDamageType()) : icon.toItemStack();
		is.offer(new MenuInventoryData(true));

		List<Text> lore = new ArrayList<>();
		List<Text> desc = getDescription();
		Text skillTargetType = null;
		if (this instanceof Targeted) {
			skillTargetType = Localizations.SKILL_TYPE_TARGETTED.toText();
		} else if (this instanceof ActiveSkill) {
			skillTargetType = Localizations.SKILL_TYPE_ACTIVE.toText();
		} else if (this instanceof PassiveSkill) {
			skillTargetType = Localizations.SKILL_TYPE_PASSIVE.toText();
		}
		if (desc != null) {
			lore.addAll(desc);
		}
		if (skillTargetType != null) {
			lore.add(Text.of(skillTargetType, TextColors.DARK_PURPLE, TextStyles.ITALIC));
		}
		lore.add(Text.EMPTY);

		int minPlayerLevel = skillData.getMinPlayerLevel();
		int maxSkillLevel = skillData.getMaxSkillLevel();
		PlayerSkillContext ei = character.getSkill(getId());
		int currentLevel = 0;
		int totalLevel = 0;
		if (ei != null) {
			currentLevel = ei.getLevel();
			totalLevel = ei.getTotalLevel();
		}

		Text s = Localizations.MIN_PLAYER_LEVEL.toText();
		if (minPlayerLevel > 0) {
			lore.add(Text.builder().append(s).color(TextColors.YELLOW)
					.append(Text.builder(" " + minPlayerLevel)
							.color(character.getLevel() < minPlayerLevel ? TextColors.RED : TextColors.GREEN)
							.build())
					.build());
		}

		s = Localizations.MAX_SKILL_LEVEL.toText(Arg.arg("level", maxSkillLevel));
		lore.add(Text.builder().append(s)
				.color(TextColors.YELLOW)
				.build());


		lore.add(Text.EMPTY);
		lore.add(Localizations.SKILL_LEVEL.toText(Arg.arg("level", currentLevel).with("total", totalLevel)));

		if (getLore() != null) {
			lore.addAll(getLore());
		}

		is.offer(Keys.ITEM_LORE, lore);

		Text skillName = skillTree.getSkillById(getId()).getSkillName();

		TextColor textColor = character.hasSkill(this.getName()) ? TextColors.GREEN : TextColors.GRAY;

		is.offer(Keys.DISPLAY_NAME,
				Text.builder(skillName.toPlain()).color(textColor).style(TextStyles.BOLD).build());
		return is;
	}

	default SkillContext createSkillExecutorContext(PlayerSkillContext esi) {
		return new SkillContext();
	}
}
