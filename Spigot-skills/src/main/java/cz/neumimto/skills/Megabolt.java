package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:megabolt")
public class Megabolt extends ActiveSkill<ISpigotCharacter> {

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
    public void cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        Player player = character.getPlayer();
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        double radius = skillContext.getDoubleNodeValue(SkillNodes.RADIUS);

        for (Entity nearbyEntity : player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                IEntity iEntity = spigotEntityService.get(livingEntity);
                if (!iEntity.isFriendlyTo(character)) {
                    damageService.damage(player, livingEntity, EntityDamageEvent.DamageCause.LIGHTNING, damage, false);
                }
            }
        }
    }
}
