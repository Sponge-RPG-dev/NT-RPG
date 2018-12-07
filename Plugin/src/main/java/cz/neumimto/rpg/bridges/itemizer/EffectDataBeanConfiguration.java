package cz.neumimto.rpg.bridges.itemizer;

import com.google.common.reflect.TypeToken;
import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import cz.neumimto.rpg.effects.EffectDataBean;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.EffectsData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;

import java.util.List;

public class EffectDataBeanConfiguration implements IItemBeanConfiguration {

    private List<EffectDataBean> bean;

    public EffectDataBeanConfiguration(ConfigurationNode node) throws ObjectMappingException {
        bean = node.getList(TypeToken.of(EffectDataBean.class));
    }

    @Override
    public Key getKey() {
        return NKeys.ITEM_EFFECTS;
    }

    @Override
    public DataManipulator<?, ?> constructDataManipulator() {
        return new EffectsData(bean);
    }
}
