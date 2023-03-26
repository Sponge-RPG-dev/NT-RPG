package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;

@AutoService(IPersistenceHandler.class)
@Singleton
public class FlatFilePersistenceHandler implements IPersistenceHandler {

}
