package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:lightningstrike")
public class LightningStrike extends TargetedEntitySkill {

    @Inject
    private EffectService effectService;

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private SpigotRpgPlugin plugin;

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.CONTACT.name());
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.LIGHTNING);
        addSkillType(SkillType.DAMAGE_CHECK_TARGET);
        settings.addExpression(SkillNodes.DAMAGE, "15 + level * 2");
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext skillContext) {
        LivingEntity entity = (LivingEntity) target.getEntity();

        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        Location location = entity.getLocation();
        location.getWorld().strikeLightningEffect(location);
        if (damage > 0) {
            damageService.damage(entity, source.getEntity(), EntityDamageEvent.DamageCause.LIGHTNING, damage, false);
        }

        return SkillResult.OK;
    }

}
