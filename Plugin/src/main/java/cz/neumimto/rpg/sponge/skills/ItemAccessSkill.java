package cz.neumimto.rpg.sponge.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.sponge.gui.GuiHelper;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.items.SpongeRpgItemType;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.AbstractSkill;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;


public class ItemAccessSkill extends AbstractSkill {

	@Inject
	private ItemService itemService;

	public ItemAccessSkill() {
		super();
	}

	@Override
	public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
		skillContext.result(SkillResult.CANCELLED);
	}

	@Override
	public void skillLearn(IActiveCharacter IActiveCharacter) {
		super.skillLearn(IActiveCharacter);
		resolveItemAccess(IActiveCharacter);

	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
		resolveItemAccess(IActiveCharacter);
	}

	@Override
	public void onCharacterInit(IActiveCharacter c, int level) {
		super.onCharacterInit(c, level);
		resolveItemAccess(c);
	}

	@Override
	public void skillRefund(IActiveCharacter IActiveCharacter) {
		super.skillRefund(IActiveCharacter);
		resolveItemAccess(IActiveCharacter);
	}

	private void resolveItemAccess(IActiveCharacter c) {
		c.updateItemRestrictions();
	}


	@Override
	public IEffectSource getType() {
		return EffectSourceType.ITEM_ACCESS_SKILL;
	}


	@Override
	public ItemAccessSkillData constructSkillData() {
		return new ItemAccessSkillData(getName());
	}

	@Override
	public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
		ItemAccessSkillData data = (ItemAccessSkillData) skillData;
		try {

			List<? extends Config> items = c.getConfigList("Items");
			for (Config item : items) {
				int level = item.getInt("level");
				List<String> citems = item.getStringList("items");
				for (String allowedWeapon : citems) {
					ItemString parsed = ItemString.parse(allowedWeapon);
					Optional<RpgItemType> type = itemService.getRpgItemType(parsed.itemId, parsed.model);
					if (type.isPresent()) {
						ClassItem citem = itemService.createClassItemSpecification(type.get(), parsed.damage, this);

						data.addItemType(level, citem);
					}
				}
			}
		} catch (ConfigException e) {

		}
	}

	@Override
	public List<ItemStack> configurationToItemStacks(SkillData skillData) {
		List<ItemStack> list = new ArrayList<>();
		ItemAccessSkillData data = (ItemAccessSkillData) skillData;
		for (Map.Entry<Integer, Set<ClassItem>> entry : data.items.entrySet()) {
				list.addAll(
						entry.getValue().stream()
						.map(SpongeRpgItemType.class::cast)
						.map(GuiHelper::rpgItemTypeToItemStack)
						.map(a -> {
							List<Text> texts = a.get(Keys.ITEM_LORE).get();
							texts.add(Text.EMPTY);
							texts.add(Localizations.SKILL_LEVEL.toText(Arg.arg("level", entry.getKey())));
							a.offer(Keys.ITEM_LORE, texts);
							a.offer(new MenuInventoryData(true));
							return a;
						}).collect(Collectors.toList()));

			}
		return list;
	}

	public class ItemAccessSkillData extends SkillData {

		private Map<Integer, Set<ClassItem>> items = new HashMap<>();

		public ItemAccessSkillData(String skill) {
			super(skill);
		}

		public Map<Integer, Set<ClassItem>> getItems() {
			return items;
		}

		public void setItems(Map<Integer, Set<ClassItem>> items) {
			this.items = items;
		}

		public void addItemType(Integer level, ClassItem item) {
			Set<ClassItem> set = items.get(level);
			if (set == null) {
				set = new HashSet<>();
				set.add(item);
				items.put(level, set);
			} else {
				set.add(item);
			}
		}
	}
}
