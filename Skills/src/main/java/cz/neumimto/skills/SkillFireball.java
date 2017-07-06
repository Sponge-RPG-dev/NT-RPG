package cz.neumimto.skills;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.SkillLocalization;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.world.World;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill
public class SkillFireball extends ActiveSkill {

	public SkillFireball() {
		setName("Fireball");
		setLore(SkillLocalization.SKILL_FIREBALL_LORE);
		setDamageType(DamageTypes.FIRE);
		setDescription(SkillLocalization.SKILL_FIREBALL_DESC);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.DAMAGE, 10, 10);
		skillSettings.addNode(SkillNodes.VELOCITY, 1.5f, .5f);
		settings = skillSettings;
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier skillModifier) {
		Player p = character.getPlayer();
		World world = p.getWorld();
		Entity optional = world.createEntity(EntityTypes.SNOWBALL, p.getLocation().getPosition().add(cos((p.getRotation().getX() - 90) % 360) * 0.2, 1.8, sin((p.getRotation().getX() - 90) % 360) * 0.2));

		Vector3d rotation = p.getRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		Snowball sb = (Snowball) optional;
		sb.offer(Keys.VELOCITY, direction.mul(settings.getLevelNodeValue(SkillNodes.VELOCITY, info.getTotalLevel())));
		sb.setShooter(p);
		world.spawnEntity(sb, Cause.of(NamedCause.of("player", character.getPlayer())));
		sb.offer(Keys.FIRE_TICKS, 999);
		ProjectileProperties projectileProperties = new ProjectileProperties(sb, character);
		projectileProperties.setDamage(settings.getLevelNodeValue(SkillNodes.DAMAGE, info.getTotalLevel()));
		SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
		build.fromSkill(this);
		build.setCaster(character);
		build.type(getDamageType());
		projectileProperties.onHit((caster, target) -> {
			target.getEntity().damage(projectileProperties.getDamage(), build.build());
		});
		return SkillResult.OK;
	}
}
