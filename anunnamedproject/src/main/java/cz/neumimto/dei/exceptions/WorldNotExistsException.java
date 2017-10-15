package cz.neumimto.dei.exceptions;

/**
 * Created by ja on 8.7.16.
 */
public class WorldNotExistsException extends RuntimeException {
	public WorldNotExistsException(String world) {
		super(world);
	}
}
