package cz.neumimto.rpg.bridges.itemizer;

import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.service.IItemBeanFactory;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class EffectBeanFactory implements IItemBeanFactory {
    @Override
    public String getKeyId() {
        return "effect";
    }

    @Override
    public IItemBeanConfiguration build(ConfigurationNode node) {
        try {
            return new EffectDataBeanConfiguration(node);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
