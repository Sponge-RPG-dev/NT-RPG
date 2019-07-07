package cz.neumimto.rpg.api.configuration;

/**
 * Created by NeumimTo on 4.11.2018.
 */
public class SkillItemCost {

    private ItemString item;
    private Integer modelId;
    private Integer amount;
    private boolean consumeItems;

    public SkillItemCost() {
    }


    public ItemString getItemType() {
        return item;
    }

    public void setItemType(ItemString itemType) {
        this.item = itemType;
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
