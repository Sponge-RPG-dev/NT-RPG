package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Path;
import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.logging.Log;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectsAdapter implements TypeSerializer<Map<IGlobalEffect, EffectParams>> {


    @Override
    public Map<IGlobalEffect, EffectParams> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        List<EffectConfigModel> list = value.getList(TypeToken.of(EffectConfigModel.class));
        Map<IGlobalEffect, EffectParams> params = new HashMap<>();
        for (EffectConfigModel model : list) {
            if (model.type == null) {
                Log.warn("Cannot read effects section - Missing node Id");
                continue;
            }
            IGlobalEffect globalEffect = Rpg.get().getEffectService().getGlobalEffect(model.type);
            if (globalEffect == null) {
                Log.error("Unknown Effect " + model.type);
                continue;
            }
            if (model.settings == null) {
                model.settings = new HashMap<>();
            }
            params.put(globalEffect, new EffectParams(model.settings));
        }
        return params;
    }

    @Override
    public void serialize(TypeToken<?> type, Map<IGlobalEffect, EffectParams> obj, ConfigurationNode value) throws ObjectMappingException {
        List<EffectConfigModel> list = new ArrayList<>();
        for (Map.Entry<IGlobalEffect, EffectParams> entry : obj.entrySet()) {
            EffectConfigModel model = new EffectConfigModel();
            model.settings = entry.getValue();
            model.type = entry.getKey().getName();
        }
        value.setValue(list);
    }

    protected static class EffectConfigModel {

        @Path("Id")
        private String type;

        @Path("Settings")
        private Map<String, String> settings;
    }
}
