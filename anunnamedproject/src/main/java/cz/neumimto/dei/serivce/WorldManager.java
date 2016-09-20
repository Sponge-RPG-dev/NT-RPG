package cz.neumimto.dei.serivce;


import cz.neumimto.dei.entity.database.area.ClaimedArea;
import cz.neumimto.dei.entity.database.area.TownClaim;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private World world;

    private Map<DChunk, ClaimedArea> claimedAreaMap = new HashMap<>();

    public WorldManager(World world) {
        this.world = world;
    }

    public ClaimedArea getClaimedArea(DChunk chunk) {
        return claimedAreaMap.get(chunk);
    }

    public void addClaim(ClaimedArea object) {
        claimedAreaMap.put(DChunk.get(object), object);
    }
}
