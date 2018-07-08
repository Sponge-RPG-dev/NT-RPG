/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.effects.common.positive;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 23.7.2015.
 */

/**
 * An example class how to use Classgenerator.
 * <p>
 * The annotation will generate according global effect class at runtime.
 * id - field name of unique identifier (in most cases its name), the field must be static and public
 * inject - If set to true the class loader tries to inject public static field which is assingable from IGlobalEffect.
 * Main behavior of global effects is that they are accessible via effectservice.getGlobalEffect(stringId) inject option is
 * here only if someone would like to keep direct field reference to the global effect object.
 * Global Effects may be given to player via command or as an item enchantement
 * <p>
 * The class, which inherits from IEffect(or its implementations such as effect base) must contain a constructor - IEffectConsumer, long duration, int level.
 * <p>
 * Global effects can work as item enchantments, and be accessible from commands
 */
@ClassGenerator.Generate(id = "name", inject = true, description = "An effect which increases target walk speed")
public class SpeedBoost extends EffectBase {

	public static final String name = "Speed";

	public static IGlobalEffect<SpeedBoost> global;

	private float speedbonus;
	private IActiveCharacter character;


	public SpeedBoost(IEffectConsumer consumer, long duration, float speedbonus) {
		super(name, consumer);
		setDuration(duration);
		this.speedbonus = speedbonus;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public void onApply() {
		super.onApply();
		getConsumer().setProperty(DefaultProperties.walk_speed,
				getGlobalScope().characterService.getCharacterProperty(getConsumer(), DefaultProperties.walk_speed) + speedbonus);
		getGlobalScope().characterService.updateWalkSpeed(getConsumer());
		Location<World> location = getConsumer().getLocation();

		ParticleEffect build = ParticleEffect.builder()
				.type(ParticleTypes.CLOUD)
				.velocity(new Vector3d(0, 0.8, 0))
				.quantity(2).build();
		Vector3d[] smallCircle = ParticleDecorator.smallCircle;

		for (Vector3d vector3d : smallCircle) {
			location.getExtent().spawnParticles(build, location.getPosition().add(vector3d));
		}

		getConsumer().sendMessage(ChatTypes.CHAT, Localizations.SPEED_BOOST_APPLY.toText());

	}

	@Override
	public void onRemove() {
		super.onRemove();
		getConsumer().setProperty(DefaultProperties.walk_speed,
				getGlobalScope().characterService.getCharacterProperty(getConsumer(), DefaultProperties.walk_speed) - speedbonus);
		getGlobalScope().characterService.updateWalkSpeed(getConsumer());
		getConsumer().sendMessage(ChatTypes.CHAT, Localizations.SPEED_BOOST_EXPIRE.toText());
	}

	@Override
	public boolean requiresRegister() {
		return true;
	}


}
