package cz.neumimto.rpg.persistence.flatfiles;

import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFileCharacterClassDao;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFilePersistenceHandler;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFilePlayerDao;

import java.util.HashMap;
import java.util.Map;


public class JPAModule implements RpgAddon {

    @Override
    public Map<Class<?>, Class<?>> getBindings() {
        Map bindings = new HashMap<>();
        bindings.put(ICharacterClassDao.class, FlatFileCharacterClassDao.class);
        bindings.put(IPlayerDao.class, FlatFilePlayerDao.class);
        bindings.put(IPersistenceHandler.class, FlatFilePersistenceHandler.class);

        return bindings;
    }

}
