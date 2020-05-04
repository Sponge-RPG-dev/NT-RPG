package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.TargetedEntitySkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:lightning")
public class Lightning extends TargetedEntitySkill {

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.MAGIC.name());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.VELOCITY, 1.5f, .5f);

        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.LIGHTNING);
        addSkillType(SkillType.CANNOT_BE_SELF_CASTED);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter character, PlayerSkillContext  skillContext) {
        LivingEntity livingEntity = (LivingEntity) target.getEntity();
        if (damageService.canDamage(character, livingEntity)) {

            return SkillResult.OK;
        }

        return SkillResult.CANCELLED;
    }
}
