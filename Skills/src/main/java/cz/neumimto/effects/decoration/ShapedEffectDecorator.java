package cz.neumimto.effects.decoration;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.effect.particle.ParticleEffect;

/**
 * Created by NeumimTo on 29.7.2017.
 */
public abstract class ShapedEffectDecorator<Value> extends EffectBase<Value> {

	protected int printerCount;

	int iter = 0;
	public ShapedEffectDecorator(String name, IEffectConsumer consumers, Vector3d[]particles) {
		super(name, consumers);
		setPeriod(7L);
	}

	@Override
	public void onTick() {
		int i = getVertices().length / printerCount;
		for (int j = 0; j < printerCount; j++) {
			int v = i * j + iter;
			draw(getVertices()[getIndex(v)]);

		}
		iter++;
	}

	public abstract void draw(Vector3d vec);

	public abstract Vector3d[] getVertices();

	private int getIndex(int i) {
		if (i < 0)
			return 0;
		if (i > getVertices().length) {
			return Math.abs(getVertices().length - i);
		}
		return i;
	}
}
