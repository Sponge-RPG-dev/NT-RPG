package cz.neumimto.rpg.common.skills.types;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.effects.EffectSourceType;
import cz.neumimto.rpg.common.effects.IEffectSource;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
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


public class ItemAccessSkill extends AbstractSkill<IActiveCharacter> {

    @Inject
    private ItemService itemService;

    @Inject
    private InventoryService inventoryService;

    public ItemAccessSkill() {
        super();
    }

    @Override
    public SkillResult onPreUse(IActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(IActiveCharacter, context);
        resolveItemAccess(IActiveCharacter);

    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(IActiveCharacter, level, context);
        resolveItemAccess(IActiveCharacter);
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        resolveItemAccess(c);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(IActiveCharacter, context);
        resolveItemAccess(IActiveCharacter);
    }

    private void resolveItemAccess(IActiveCharacter c) {
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
