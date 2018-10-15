package cz.neumimto.skills.active;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ProjectileProperties;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill("ntrpg:fireball")
public class SkillFireball extends ActiveSkill {

	public void init() {
		super.init();
		setDamageType(DamageTypes.FIRE);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.DAMAGE, 10, 10);
		skillSettings.addNode(SkillNodes.VELOCITY, 1.5f, .5f);
		settings = skillSettings;
		addSkillType(SkillType.SUMMON);
		addSkillType(SkillType.PROJECTILE);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.FIRE);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillContext) {
		Player p = character.getPlayer();
		World world = p.getWorld();
		Entity optional = world.createEntity(EntityTypes.SNOWBALL, p.getLocation().getPosition()
				.add(cos((p.getRotation().getX() - 90) % 360) * 0.2, 1.8, sin((p.getRotation().getX() - 90) % 360) * 0.2));

		Vector3d rotation = p.getRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		Snowball sb = (Snowball) optional;
		sb.offer(Keys.VELOCITY, direction.mul(getFloatNodeValue(info, SkillNodes.VELOCITY)));
		sb.setShooter(p);
		world.spawnEntity(sb);
		sb.offer(Keys.FIRE_TICKS, 999);
		ProjectileProperties projectileProperties = new ProjectileProperties(sb, character);
		projectileProperties.setDamage(getDoubleNodeValue(info, SkillNodes.DAMAGE));
		SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
		build.fromSkill(this);
		build.setCaster(character);
		build.type(getDamageType());
		projectileProperties.onHit((event, caster, target) -> {
			target.getEntity().damage(projectileProperties.getDamage(), build.build());
		});
		return skillContext.next(character, info, SkillResult.OK);
	}
}
