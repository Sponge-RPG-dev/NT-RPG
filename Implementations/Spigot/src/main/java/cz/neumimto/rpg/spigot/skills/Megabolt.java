package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:megabolt")
public class Megabolt extends ActiveSkill<ISpigotCharacter> {

    @Inject
    private SpigotDamageService spigotDamageService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DAMAGE, "20 + level");
        settings.addExpression(SkillNodes.RADIUS, "15 + level");
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info) {
        double damage = info.getDoubleNodeValue(SkillNodes.DAMAGE);
        double radius = info.getDoubleNodeValue(SkillNodes.RADIUS);
        LivingEntity player = character.getEntity();

        for (Entity nearbyEntity : player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) nearbyEntity;
                if (spigotDamageService.damage(target, player, EntityDamageEvent.DamageCause.LIGHTNING, damage, false)) {
                    target.getLocation().getWorld().strikeLightningEffect(target.getLocation());
                }
            }
        }

        return SkillResult.OK;
    }

}
