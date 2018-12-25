package cz.neumimto.rpg.bridges.itemizer.factory;

import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.service.IItemBeanFactory;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.ItemLevelData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;

/**
 * Created by NeumimTo on 25.12.2018.
 */
public class ItemLevelBeanFactory implements IItemBeanFactory {

    @Override
    public String getKeyId() {
        return "ItemLevel";
    }

    @Override
    public IItemBeanConfiguration build(ConfigurationNode node) {
        return new IItemBeanConfiguration() {
            @Override
            public Key getKey() {
                return NKeys.ITEM_LEVEL;
            }

            @Override
            public DataManipulator<?, ?> constructDataManipulator() {
                return new ItemLevelData(node.getInt());
            }
        };
    }
}
