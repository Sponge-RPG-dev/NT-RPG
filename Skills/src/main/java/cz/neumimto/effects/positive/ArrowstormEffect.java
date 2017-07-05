package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.skills.ProjectileProperties;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class ArrowstormEffect extends EffectBase implements IEffectContainer {

	public static final String name = "Arrowstorm";
	private int arrows;

	public ArrowstormEffect(IEffectConsumer consumer, long period, int arrows) {
		super(name, consumer);
		this.arrows = arrows;
		setDuration(-1L);
		setPeriod(period);
	}

	@Override
	public void onTick() {
		if (arrows != 0) {
			Living entity = getConsumer().getEntity();
			World world = entity.getWorld();
			world.createEntity(EntityTypes.TIPPED_ARROW,
					entity.getLocation().getPosition()
							.add(cos((entity.getRotation().getX() - 90) % 360
							) * 0.2, 1.8, sin((entity.getRotation().getX() - 90) % 360) * 0.2));
			arrows--;
		} else {
			setDuration(0); //remove the effect next effect scheduler phase
		}
	}

	@Override
	public Set<ArrowstormEffect> getEffects() {
		return new HashSet<>();
	}

	@Override
	public Object getStackedValue() {
		return null;
	}

	@Override
	public void setStackedValue(Object o) {

	}
}
