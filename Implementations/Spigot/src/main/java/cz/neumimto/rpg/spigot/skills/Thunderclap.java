package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.scripting.SpigotScriptFunctions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:thunderclap")
public class Thunderclap extends ActiveSkill<ISpigotCharacter> {

    @Inject
    private SpigotEntityService spigotEntityService;

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.LIGHTNING.name());
        settings.addNode(SkillNodes.DAMAGE, 30, 10);
        settings.addNode(SkillNodes.RADIUS, 8f, 1f);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.LIGHTNING);
    }


    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        double radius = skillContext.getDoubleNodeValue(SkillNodes.RADIUS);

        for (Entity nearbyEntity : player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                if (damageService.canDamage(character, livingEntity)) {
                    SpigotScriptFunctions.SPAWN_LIGHTNING.accept(livingEntity.getLocation());
                    damageService.damage(player, livingEntity, EntityDamageEvent.DamageCause.LIGHTNING, damage, false);
                }
            }
        }
        return SkillResult.OK;
    }
}
