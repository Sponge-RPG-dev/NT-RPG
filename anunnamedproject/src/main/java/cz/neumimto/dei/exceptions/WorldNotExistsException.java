package cz.neumimto.dei.exceptions;

import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by ja on 8.7.16.
 */
public class WorldNotExistsException extends RuntimeException {
    public WorldNotExistsException(String world) {
        super(world);
    }
}
