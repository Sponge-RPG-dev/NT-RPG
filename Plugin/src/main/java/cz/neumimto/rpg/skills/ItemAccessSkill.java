package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.gui.GuiHelper;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;


public class ItemAccessSkill extends AbstractSkill {

    @Inject
    private ItemService itemService;

    public ItemAccessSkill(String name) {
        super();
        setName(name);
        setIcon(ItemTypes.REDSTONE);
    }

    @Override
    public SkillResult onPreUse(IActiveCharacter character) {
        return SkillResult.CANCELLED;
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
                    String[] split = allowedWeapon.split(";");
                    String s = split[0];
                    double damage = 0;
                    String itemName = null;
                    ItemType type = Sponge.getGame().getRegistry().getType(ItemType.class, s).orElse(null);
                    if (type == null) {
                        errors.log(" - Unknown item type " + s);
                    } else {
                        String s1 = split[1];
                        damage = Double.parseDouble(s1);
                        if (split.length == 3) {
                            itemName = split[2];
                        }
                        ConfigRPGItemType t = new ConfigRPGItemType(itemService.getByItemTypeAndName(type,itemName), data.getSkill(), damage);
                        data.addItemType(level, t);
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
        for (Map.Entry<Integer, Map<ItemType, Set<ConfigRPGItemType>>> entry : data.items.entrySet()) {
            for (Set<ConfigRPGItemType> configRPGItemTypes : entry.getValue().values()) {
                list.addAll(configRPGItemTypes.stream()
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
        }
        return list;
    }

    public class ItemAccessSkillData extends SkillData {

        private Map<Integer, Map<ItemType,Set<ConfigRPGItemType>>> items = new HashMap<>();

        public ItemAccessSkillData(String skill) {
            super(skill);
        }

        public Map<Integer, Map<ItemType, Set<ConfigRPGItemType>>> getItems() {
            return items;
        }

        public void addItemType(Integer level, ConfigRPGItemType type) {
            Map<ItemType, Set<ConfigRPGItemType>> itemTypeTreeSetMap = items.get(level);
            if (itemTypeTreeSetMap == null) {
                itemTypeTreeSetMap = new HashMap<>();
                Set<ConfigRPGItemType> set = new HashSet<>();
                set.add(type);
                itemTypeTreeSetMap.put(type.getRpgItemType().getItemType(), set);
                items.put(level, itemTypeTreeSetMap);
            } else {
                Set<ConfigRPGItemType> configRPGItemTypes = itemTypeTreeSetMap
                        .computeIfAbsent(type.getRpgItemType().getItemType(), k -> new HashSet<>());
                configRPGItemTypes.add(type);
            }
        }

        public void setItems(Map<Integer, Map<ItemType, Set<ConfigRPGItemType>>> items) {
            this.items = items;
        }
    }
}
