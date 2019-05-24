package cz.neumimto.rpg.sponge.skills;

import org.spongepowered.api.item.ItemType;

/**
 * Created by NeumimTo on 4.11.2018.
 */
public class SkillItemCost {

    private ItemType itemType;
    private Integer modelId;
    private Integer amount;
    private boolean consumeItems;

    public SkillItemCost() {
    }


    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setConsumeItems(boolean consumeItems) {
        this.consumeItems = consumeItems;
    }

    public boolean consumeItems() {
        return consumeItems;
    }
}
