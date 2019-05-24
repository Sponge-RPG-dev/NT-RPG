package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.logging.Log;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

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
            IGlobalEffect globalEffect = NtRpgPlugin.GlobalScope.effectService.getGlobalEffect(model.type);
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

    }

    @ConfigSerializable
    protected static class EffectConfigModel {

        @Setting("Id")
        private String type;

        @Setting("Settings")
        private Map<String, String> settings;
    }
}
