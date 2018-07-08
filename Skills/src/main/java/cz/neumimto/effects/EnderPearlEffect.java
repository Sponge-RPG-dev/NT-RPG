package cz.neumimto.effects;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.MinLongStackingStrategy;


/**
 * Created by NeumimTo on 7.8.17.
 */
@ClassGenerator.Generate(id = "name", description = "An effect which allows target to throw and  teleport via ender pearl")
public class EnderPearlEffect extends EffectBase<Long> {

	public static final String name = "Ender Pearl";

	public EnderPearlEffect(IEffectConsumer consumer, long duration, Long value) {
		super(name, consumer);
		setDuration(duration);
		setStackable(true, MinLongStackingStrategy.INSTNCE);
	}

	@Override
	public Container constructEffectContainer() {
		return new Container(this);
	}


	public static class Container extends EffectContainer<Long, EnderPearlEffect> {

		private long lastTimeUsed = 0;

		public Container(EnderPearlEffect enderPearlEffect) {
			super(enderPearlEffect);
		}

		public long getLastTimeUsed() {
			return lastTimeUsed;
		}

		public void setLastTimeUsed(long lastTimeUsed) {
			this.lastTimeUsed = lastTimeUsed;
		}
	}
}
