package cz.neumimto.dei.serivce;

import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.dei.entity.database.area.ClaimedArea;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class DChunk {

	public final int x, z;

	protected DChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public static DChunk get(int x, int z) {
		return new DChunk(x, z);
	}

	public static DChunk get(Location<World> location) {
		Vector3i chunkPosition = location.getChunkPosition();
		return get(chunkPosition.getX(), chunkPosition.getZ());
	}

	public static DChunk get(ClaimedArea object) {
		return get(object.getX(), object.getZ());
	}
}
