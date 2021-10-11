package cz.neumimto.rpg.persistence.flatfiles;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.RpgAddon;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFileCharacterClassDao;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFilePersistenceHandler;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFilePlayerDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AutoService(RpgAddon.class)
public class FlatFilesModule implements RpgAddon {

    @Override
    public Map<Class<?>, Class<?>> getBindings() {
        Map bindings = new HashMap<>();
        bindings.put(ICharacterClassDao.class, FlatFileCharacterClassDao.class);
        bindings.put(IPlayerDao.class, FlatFilePlayerDao.class);
        bindings.put(IPersistenceHandler.class, FlatFilePersistenceHandler.class);

        return bindings;
    }

    @Override
    public Map<Class<?>, ?> getProviders(Map<String, Object> implementationScope) {
        return Collections.emptyMap();
    }

}
