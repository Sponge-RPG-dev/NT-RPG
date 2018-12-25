package cz.neumimto.rpg.bridges.itemizer;

import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.service.IItemBeanFactory;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;

/**
 * Created by NeumimTo on 25.12.2018.
 */

public class headerBeanFactory implements IItemBeanFactory {

    static {


        cz.neumimto.rpg.bridges.itemizer.Itemizer.itemBeanFactories.add(headerBeanFactory.class);
    }
    @Override
    public String getKeyId() {
        return "header";
    }

    @Override
    public IItemBeanConfiguration build(final ConfigurationNode node) {
        return new IItemBeanConfiguration() {
            @Override
            public Key getKey() {
                return cz.neumimto.rpg.inventory.data.NKeys.ITEM_META_HEADER;
            }

            @Override
            public DataManipulator<?, ?> constructDataManipulator() {
                org.spongepowered.api.text.Text o = cz.neumimto.core.localization.TextHelper.parse(node.getString());

                return new cz.neumimto.rpg.inventory.data.manipulators.ItemMetaHeader(o);
            }
        };
    }
}
