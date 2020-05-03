package cz.neumimto.skills.active;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.effects.negative.SlowPotion;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.skills.ProjectileProperties;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

/**
 * Created by NeumimTo on 11.8.17.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:icebolt")
public class IceBolt extends ActiveSkill<ISpongeCharacter> {

    @Inject
    private EffectService effectService;

    @Inject
    private EntityService entityService;

    @Override
    public void init() {
        super.init();
        setDamageType(NDamageType.ICE.getId());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.VELOCITY, 0.5f, .5f);
        settings.addNode(SkillNodes.DURATION, 750, 15);
        settings.addNode(SkillNodes.AMPLIFIER, 1, 0f);
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.ICE);
    }

    @Override
    public SkillResult cast(ISpongeCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        Player p = character.getPlayer();
        World world = p.getWorld();
        Entity optional = world.createEntity(EntityTypes.SNOWBALL, p.getLocation().getPosition()
                .add(cos((p.getRotation().getX() - 90) % 360) * 0.2, 1.8, sin((p.getRotation().getX() - 90) % 360) * 0.2));

        Vector3d rotation = p.getRotation();
        Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
        Snowball sb = (Snowball) optional;
        sb.offer(Keys.VELOCITY, direction.mul(skillContext.getFloatNodeValue(SkillNodes.VELOCITY)));
        sb.setShooter(p);
        world.spawnEntity(sb);
        ProjectileProperties projectileProperties = new ProjectileProperties(sb, character);
        projectileProperties.setDamage(skillContext.getDoubleNodeValue(SkillNodes.DAMAGE));
        SkillDamageSource s = new SkillDamageSourceBuilder()
                .fromSkill(this)
                .setSource(character)
                .build();

        projectileProperties.onHit((event, caster, target) -> {
            long slowduration = skillContext.getLongNodeValue(SkillNodes.DURATION);
            int slowamplf = skillContext.getIntNodeValue(SkillNodes.AMPLIFIER);
            ((ISpongeEntity) target).getEntity().damage(projectileProperties.getDamage(), s);
            effectService.addEffect(new SlowPotion(target, slowduration, slowamplf), this);
        });
        skillContext.next(character, info, skillContext.result(SkillResult.OK));
    }
}
