package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:kick")
public class Kick extends TargetedEntitySkill {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.CONTACT.name());

        settings.addNode(SkillNodes.DAMAGE, 15, 5);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext skillContext) {
        LivingEntity entity = (LivingEntity) target.getEntity();

        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        damageService.damage(entity, source.getEntity(), EntityDamageEvent.DamageCause.CONTACT, damage, false);

        entity.setVelocity(new Vector(Math.random() * 0.4 - 0.2, 0.8, Math.random() * 0.4 - 0.2));
        entity.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, entity.getLocation(), 2);

        return SkillResult.OK;
    }

}
