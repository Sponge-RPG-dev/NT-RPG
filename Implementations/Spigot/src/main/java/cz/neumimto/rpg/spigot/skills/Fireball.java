package cz.neumimto.rpg.spigot.skills;


import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import javax.inject.Singleton;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;


@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:fireball")
public class Fireball extends ActiveSkill<SpigotCharacter> {

    @Override
    public void init() {
        super.init();
        setDamageType(DamageCause.FIRE.name());
        settings.addExpression(SkillNodes.DAMAGE, "10 + level");
        settings.addExpression(SkillNodes.VELOCITY, "1.5f");
        settings.addExpression("fireticks", "120");
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.FIRE);
    }

    @Override
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext skillContext) {
        Player p = character.getPlayer();
        World world = p.getWorld();

        SmallFireball fireball = (SmallFireball) world.spawnEntity(p.getLocation().clone().add(0, 1, 0).add(p.getLocation().getDirection()), EntityType.SMALL_FIREBALL);
        fireball.setVelocity(p.getLocation().getDirection().multiply(skillContext.getFloatNodeValue(SkillNodes.VELOCITY)));
        fireball.setShooter(p);
        fireball.setFireTicks(99);
        fireball.setIsIncendiary(false);

        ProjectileCache projectileProperties = ProjectileCache.putAndGet(fireball, character);
        projectileProperties.setSkill(skillContext);
        projectileProperties.onHit((event, attacker, target) -> {
            event.setDamage(skillContext.getDoubleNodeValue(SkillNodes.DAMAGE));
            target.getEntity().setFireTicks(skillContext.getIntNodeValue("fireticks"));
        });

        world.playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.5f, 0.5f);
        return SkillResult.OK;
    }
}

