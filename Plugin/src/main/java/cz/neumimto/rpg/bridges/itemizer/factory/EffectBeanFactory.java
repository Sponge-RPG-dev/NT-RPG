package cz.neumimto.rpg.bridges.itemizer.factory;

import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.service.IItemBeanFactory;
import cz.neumimto.rpg.effects.EffectDataBean;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.EffectsData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Object value = node.getValue();
        final List<EffectDataBean> data = new ArrayList<>();
        if (value instanceof List) {
            List<LinkedHashMap> list = (List) value;
            for (Map m : list) {
                String effectName = (String) m.get("effect");
                LinkedHashMap params = (LinkedHashMap) m.get("params");
                data.add(new EffectDataBean(effectName, new EffectParams(params)));
            }
        } else if (value instanceof Map) {
            Map m = (Map) value;
            String effectName = (String) m.get("effect");
            LinkedHashMap params = (LinkedHashMap) m.get("params");
            data.add(new EffectDataBean(effectName, new EffectParams(params)));
        }
        return new IItemBeanConfiguration() {
            @Override
            public Key getKey() {
                return NKeys.ITEM_EFFECTS;
            }

            @Override
            public DataManipulator<?, ?> constructDataManipulator() {
                return new EffectsData(data);
            }
        };
    }
}