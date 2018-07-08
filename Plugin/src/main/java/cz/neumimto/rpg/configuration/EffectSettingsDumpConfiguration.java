package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

/**
 * Created by NeumimTo on 8.7.2018.
 */
@ConfigSerializable
public class EffectSettingsDumpConfiguration {

    @Setting(value="effects", comment="List of avalaible effects and its config nodes")
    private Map<String, EffectSettingDumpConfiguration> effects;

}
