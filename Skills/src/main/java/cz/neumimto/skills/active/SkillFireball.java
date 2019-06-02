package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.skills.ProjectileProperties;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.world.World;

import javax.inject.Singleton;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:fireball")
public class SkillFireball extends ActiveSkill {

	@Override
	public void init() {
		super.init();
		setDamageType(DamageTypes.FIRE);
		settings.addNode(SkillNodes.DAMAGE, 10, 10);
		settings.addNode(SkillNodes.VELOCITY, 1.5f, .5f);
		addSkillType(SkillType.SUMMON);
		addSkillType(SkillType.PROJECTILE);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.FIRE);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		Player p = character.getPlayer();
		World world = p.getWorld();
		Entity optional = world.createEntity(EntityTypes.SNOWBALL, p.getLocation().getPosition()
				.add(cos((p.getHeadRotation().getX() - 90) % 360) * 0.2, 1.8, sin((p.getHeadRotation().getX() - 90) % 360) * 0.2));

		Vector3d rotation = p.getHeadRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		Snowball sb = (Snowball) optional;
		sb.offer(Keys.VELOCITY, direction.mul(skillContext.getFloatNodeValue(SkillNodes.VELOCITY)));
		sb.setShooter(p);
		world.spawnEntity(sb);
		sb.offer(Keys.FIRE_TICKS, 999);
		ProjectileProperties projectileProperties = new ProjectileProperties(sb, character);
		projectileProperties.setDamage(skillContext.getDoubleNodeValue(SkillNodes.DAMAGE));
		SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
		build.fromSkill(this);
		build.setSource(character);
		build.type(getDamageType());
		projectileProperties.onHit((event, caster, target) -> {
			target.getEntity().damage(projectileProperties.getDamage(), build.build());
		});
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}
}
