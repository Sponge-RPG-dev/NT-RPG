package cz.neumimto.rpg.sponge.inventory.sockets;

import cz.neumimto.rpg.common.inventory.sockets.SocketType;
import cz.neumimto.rpg.common.inventory.sockets.SocketTypes;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(SocketTypes.class)
public class SocketTypeWrapper implements CatalogType {
    private SocketType socketType;

    @Override
    public String getId() {
        return socketType.getId();
    }

    @Override
    public String getName() {
        return socketType.getName();
    }
}
