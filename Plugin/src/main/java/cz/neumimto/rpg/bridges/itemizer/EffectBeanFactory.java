package cz.neumimto.rpg.bridges.itemizer;

import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.service.IItemBeanFactory;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.EffectsData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;

import java.util.HashMap;

public class EffectBeanFactory implements IItemBeanFactory {
    @Override
    public String getKeyId() {
        return "effect";
    }

    @Override
    public IItemBeanConfiguration build(ConfigurationNode node) {
        return new IItemBeanConfiguration() {
            @Override
            public Key getKey() {
                return NKeys.ITEM_EFFECTS;
            }

            @Override
            public DataManipulator<?, ?> constructDataManipulator() {
                node.getList()
                return new EffectsData(new HashMap<>());
            }
        };
    }
}
