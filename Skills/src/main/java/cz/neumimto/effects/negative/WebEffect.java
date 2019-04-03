package cz.neumimto.effects.negative;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class WebEffect extends EffectBase<Long> {

	public static String name = "Web";
	private Vector3i[] vector3is = new Vector3i[9];

	public WebEffect(IEffectConsumer effectConsumer, long duration) {
		super(name, effectConsumer);
		setDuration(duration);
		setStackable(false, null);
	}

	@Override
	public void onApply(IEffect self) {
		super.onApply(self);
		Location<World> location = getConsumer().getEntity().getLocation();
		Vector3d position = location.getPosition();
		int floorY = position.getFloorY();
		BlockState build = BlockState.builder().blockType(BlockTypes.WEB).build();
		int b = 0;
		for (int i = -1; i <= 1; i++) {
			for (int x = -1; x <= 1; x++) {
				Vector3i vector3i = new Vector3i(
						position.getFloorX() + i,
						floorY,
						position.getFloorZ() + x);
				vector3is[b] = vector3i;
				b++;
				if (location.getExtent().getBlock(vector3i).getType() == BlockTypes.AIR) {
					location.getExtent().setBlock(
							vector3i,
							build
					);

				}
			}
		}
	}

	@Override
	public void onRemove(IEffect self) {
		super.onRemove(self);
		Location<World> location = getConsumer().getEntity().getLocation();
		BlockState build = BlockState.builder().blockType(BlockTypes.AIR).build();

		for (Vector3i vector3i : vector3is) {
			BlockState block = location.getExtent().getBlock(vector3i);
			if (block.getType() == BlockTypes.WEB) {
				location.getExtent().setBlock(
						vector3i,
						build
				);
			}
		}
	}
}
