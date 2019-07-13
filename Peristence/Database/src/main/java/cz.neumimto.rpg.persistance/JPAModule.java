package cz.neumimto.rpg.persistance;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.GuiceModule;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistance.dao.JPACharacterClassDao;
import cz.neumimto.rpg.persistance.dao.JPAPlayerDao;

import java.util.HashMap;
import java.util.Map;


public class JPAModule implements GuiceModule {

    @Override
    public Map<Class<?>, Class<?>> getBindings() {
        Map<Class<?>, Class<?>> bindings = new HashMap<>();
        bindings.put(ICharacterClassDao.class, JPACharacterClassDao.class);
        bindings.put(IPlayerDao.class, JPAPlayerDao.class);
        bindings.put(IPersistenceHandler.class, JPAPersistenceHandler.class);
        return bindings;
    }

    @Override
    public void processStageEarly(Injector injector) {
        IPersistenceHandler instance = injector.getInstance(IPersistenceHandler.class);
    }
}
