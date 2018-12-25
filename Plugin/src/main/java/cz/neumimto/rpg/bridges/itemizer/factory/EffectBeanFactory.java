package cz.neumimto.rpg.bridges.itemizer.factory;

import com.google.common.reflect.TypeToken;
import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.service.IItemBeanFactory;
import cz.neumimto.rpg.effects.EffectDataBean;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.EffectsData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;

import java.util.List;

/**
 * Created by NeumimTo on 25.12.2018.
 */
public class EffectBeanFactory implements IItemBeanFactory {

    @Override
    public String getKeyId() {
        return "Effects";
    }

    @Override
    public IItemBeanConfiguration build(final ConfigurationNode node) {
        return new IItemBeanConfiguration() {
            @Override
            public Key getKey() {
                return NKeys.ITEM_EFFECTS;
            }

            @Override
            public DataManipulator<?, ?> constructDataManipulator() {
                List<EffectDataBean> o = null;
                try {
                    o = node.getList(TypeToken.of(EffectDataBean.class));
                } catch (ObjectMappingException e) {
                    throw new RuntimeException(e);
                }
                return new EffectsData(o);
            }
        };
    }
}