package cz.neumimto.rpg.common.skills.types;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.effects.EffectSourceType;
import cz.neumimto.rpg.common.effects.IEffectSource;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillExecutionType;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;

import javax.inject.Inject;
import java.util.*;


public class ItemAccessSkill extends AbstractSkill<ActiveCharacter> {

    @Inject
    private ItemService itemService;

    @Inject
    private InventoryService inventoryService;

    public ItemAccessSkill() {
        super();
    }

    @Override
    public SkillResult onPreUse(ActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void skillLearn(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(ActiveCharacter, context);
        resolveItemAccess(ActiveCharacter);

    }

    @Override
    public void skillUpgrade(ActiveCharacter ActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(ActiveCharacter, level, context);
        resolveItemAccess(ActiveCharacter);
    }

    @Override
    public void onCharacterInit(ActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        resolveItemAccess(c);
    }

    @Override
    public void skillRefund(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(ActiveCharacter, context);
        resolveItemAccess(ActiveCharacter);
    }

    private void resolveItemAccess(ActiveCharacter c) {
        c.updateItemRestrictions();
        inventoryService.invalidateGUICaches(c);
    }


    @Override
    public IEffectSource getType() {
        return EffectSourceType.ITEM_ACCESS_SKILL;
    }


    @Override
    public ItemAccessSkillData constructSkillData() {
        return new ItemAccessSkillData(getId());
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
                    Optional<RpgItemType> type = itemService.getRpgItemType(parsed.itemId, parsed.variant);
                    if (type.isPresent()) {
                        data.addItemType(level, type.get());
                    }
                }
            }
        } catch (ConfigException e) {

        }
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    public class ItemAccessSkillData extends SkillData {

        private Map<Integer, Set<RpgItemType>> items = new HashMap<>();

        public ItemAccessSkillData(String skill) {
            super(skill);
        }

        public Map<Integer, Set<RpgItemType>> getItems() {
            return items;
        }

        public void setItems(Map<Integer, Set<RpgItemType>> items) {
            this.items = items;
        }

        public void addItemType(Integer level, RpgItemType item) {
            Set<RpgItemType> set = items.get(level);
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
