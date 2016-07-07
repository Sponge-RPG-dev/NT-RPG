package cz.neumimto.dei.listeners;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.dei.ListenerClass;
import cz.neumimto.dei.entity.database.area.ClaimedArea;
import cz.neumimto.dei.entity.database.player.Citizen;
import cz.neumimto.dei.serivce.PlayerService;
import cz.neumimto.dei.serivce.WorldService;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 5.7.2016.
 */
@ListenerClass
@Singleton
public class Movement {

    @Inject
    private PlayerService playerService;

    @Inject
    private WorldService worldService;

    @Listener
    public void onEntityMovement(MoveEntityEvent e) {
        Transform<World> f = e.getFromTransform();
        Transform<World> t = e.getToTransform();
        if (f.getLocation().getBlockX() == t.getLocation().getBlockX() &&
                t.getLocation().getBlockZ() == f.getLocation().getBlockZ()) {
            return;
        }
        int x = t.getLocation().getBlockX();
        int z = t.getLocation().getBlockZ();
        if (x == (x >> 4) << 4 && z == (z >> 4) << 4) {
            EntityType type = e.getTargetEntity().getType();
            if (type == EntityTypes.PLAYER) {
                Citizen citizen = playerService.getCitizen(e.getTargetEntity().getUniqueId());
                ClaimedArea nextArea = worldService.getClaimedArea(t.getLocation());
                worldService.handleChunkChange(citizen, nextArea);
            }
        }
    }
}
