package cz.neumimto.rpg.gui;

import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.item.UseLimitProperty;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by NeumimTo on 11.2.2018.
 */
public class SkillTreeInterfaceModel {

    private final int damage;
    private final ItemType itemType;
    private final Text name;
    private final short id;

    public SkillTreeInterfaceModel(Integer damage, ItemType itemType, String name, short id) {
        this.damage = damage;
        this.itemType = itemType;
        this.name = TextHelper.parse(name);
        this.id = id;
    }

    public ItemStack toItemStack() {
        ItemStack of = ItemStack.of(itemType, 1);
        of.offer(Keys.HIDE_MISCELLANEOUS, true);
        of.offer(Keys.HIDE_ATTRIBUTES, true);
        of.offer(Keys.HIDE_UNBREAKABLE, true);
        Optional<UseLimitProperty> itemdamage = of.getProperty(UseLimitProperty.class);
        itemdamage.ifPresent(useLimitProperty -> {
            of.offer(Keys.ITEM_DURABILITY, useLimitProperty.getValue() - damage);
            of.offer(Keys.UNBREAKABLE, true);
        });
        of.offer(Keys.DISPLAY_NAME, name);
        of.offer(new MenuInventoryData(true));
        return of;
    }

    public short getId() {
        return id;
    }
}
