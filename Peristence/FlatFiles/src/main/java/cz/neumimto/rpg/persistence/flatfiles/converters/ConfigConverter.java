package cz.neumimto.rpg.persistence.flatfiles.converters;

import com.electronwill.nightconfig.core.Config;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;

public class ConfigConverter {

    public Config toConfig(CharacterBase characterBase) {
        Config config = Config.inMemory();
    }
}
