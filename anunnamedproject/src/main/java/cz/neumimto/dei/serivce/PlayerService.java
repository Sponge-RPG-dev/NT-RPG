package cz.neumimto.dei.serivce;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.dei.entity.database.player.Citizen;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ja on 5.7.2016.
 */
@Singleton
public class PlayerService {

    @Inject
    private WorldService worldService;

    private Map<UUID,Citizen> players = new HashMap<>();

    public void updateCache(UUID uuid) {
        Citizen citizen = players.get(uuid);
        if (citizen == null) {

        } else {

        }
    }

    public Citizen getCitizen(UUID uuid) {
        return players.get(uuid);
    }
}
