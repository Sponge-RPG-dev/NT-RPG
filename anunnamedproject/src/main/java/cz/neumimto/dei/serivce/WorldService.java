package cz.neumimto.dei.serivce;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.dei.DEI;
import cz.neumimto.dei.entity.IHasClaims;
import cz.neumimto.dei.entity.dao.TownDAO;
import cz.neumimto.dei.entity.dao.WorldDao;
import cz.neumimto.dei.entity.database.area.ClaimedArea;
import cz.neumimto.dei.entity.database.area.TownClaim;
import cz.neumimto.dei.entity.database.player.Citizen;
import cz.neumimto.dei.entity.database.worldobject.Town;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

/**
 * Created by ja on 5.7.2016.
 */
@Singleton
public class WorldService {

    @Inject
    private PlayerService playerService;

    @Inject
    private TownDAO townDAO;

    @Inject
    private DEI plugin;

    @Inject
    private WorldDao worldDao;

    Map<String,WorldManager> enabledWorlds = new HashMap<>();

    public ClaimedArea getClaimedArea(Location<World> location) {
        WorldManager worldManager = enabledWorlds.get(location.getExtent().getName());
        if (worldManager == null) {
            return null;
        }
        return worldManager.getClaimedArea(DChunk.get(location));
    }

    public void handleChunkChange(Citizen citizen, ClaimedArea nextArea) {
        ClaimedArea currentChunk = citizen.getCurrentChunk();
        if (currentChunk == nextArea) {
            return;
        }
        if (currentChunk.getParent() == nextArea.getParent()) {
            return;
        }
    }

    public void claimChunk(Town town, TownClaim townClaim) {
        townClaim.setParent(town);
        town.getClaimedAreas().add(townClaim);
    }

    public void updateAsync(final Town town) {
        Sponge.getScheduler().createTaskBuilder().async().execute(()->update(town)).submit(plugin);
    }

    public void update(Town town) {
        townDAO.update(town);
    }

    public UnclaimActionResult unclaimChunk(Location location) {
        ClaimedArea claimedArea = getClaimedArea(location);
        if (claimedArea == null) {
            return UnclaimActionResult.NOT_CLAIMED;
        }
        IHasClaims parent = claimedArea.getParent();
        ClaimedAreaType type = parent.getType();
        return UnclaimActionResult.OK;
    }

    public void loadClaimedChunks(String world) {
        List<TownClaim> objects = worldDao.loadWorldTowns(world);
        WorldManager worldManager = enabledWorlds.get(world);
        for (TownClaim object : objects) {
            worldManager.addClaim(object);
        }
    }

    public boolean isWorldEnabled(World world) {
        return enabledWorlds.containsKey(world.getName());
    }
}
