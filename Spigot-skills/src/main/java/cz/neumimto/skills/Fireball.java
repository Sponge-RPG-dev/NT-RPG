package cz.neumimto.skills;


import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;


@Singleton
@ResourceLoader.Skill("ntrpg:fireball")
public class Fireball extends ActiveSkill<ISpigotCharacter> {

    @Inject
    private SpigotDamageService spigotDamageService;

    private Fireball self;

    @Override
    public void init() {
        super.init();
        setDamageType(DamageCause.FIRE.name());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.VELOCITY, 1.5f, .5f);
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.FIRE);
        this.self = this;
    }

    @Override
    public void cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        Player p = character.getPlayer();
        World world = p.getWorld();

        SmallFireball fireball = (SmallFireball) world.spawnEntity(p.getLocation().add(p.getLocation().getDirection()), EntityType.SMALL_FIREBALL);
        fireball.setVelocity(p.getLocation().getDirection().multiply(skillContext.getFloatNodeValue(SkillNodes.VELOCITY)));
        fireball.setShooter(p);
        fireball.setFireTicks(99);


        ProjectileCache projectileProperties = ProjectileCache.putAndGet(fireball, character);
        projectileProperties.setSkill(info);
        projectileProperties.onHit((event, attacker, target) -> {
            event.setDamage(skillContext.getDoubleNodeValue(SkillNodes.DAMAGE));
        });
        skillContext.next(character, info, skillContext.result(SkillResult.OK));
    }
}

