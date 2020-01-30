package cz.neumimto.rpg.spigot.skills;


import com.google.inject.Inject;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.skill.SkillTargetAttemptEvent;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.types.ITargeted;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public abstract class Targeted extends ActiveSkill<ISpigotCharacter> implements ITargeted<ISpigotCharacter> {

    @Inject
    protected SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.RANGE, 10, 10);
    }

    @Override
    public void cast(ISpigotCharacter caster, PlayerSkillContext info, SkillContext skillContext) {
        int range = skillContext.getIntNodeValue(SkillNodes.RANGE);
        LivingEntity l = rayTraceEntity(caster.getPlayer(), range);
        if (l == null) {
            if (getDamageType() == null && !getSkillTypes().contains(SkillType.CANNOT_BE_SELF_CASTED)) {
                l = caster.getEntity();
            } else {
                skillContext.next(caster, info, SkillResult.NO_TARGET); //dont chain
                return;
            }
        }
        if (getDamageType() != null && !damageService.canDamage(caster, l)) {
            skillContext.next(caster, info, SkillResult.CANCELLED); //dont chain
            return;
        }
        IEntity<LivingEntity> target = Rpg.get().getEntityService().get(l);

        SkillTargetAttemptEvent event = Rpg.get().getEventFactory().createEventInstance(SkillTargetAttemptEvent.class);
        event.setSkill(this);
        event.setCaster(caster);
        event.setTarget(target);

        if (Rpg.get().postEvent(event)) {
            //todo https://github.com/Sponge-RPG-dev/NT-RPG/issues/111
            skillContext.next((IActiveCharacter) event.getCaster(), info, SkillResult.CANCELLED); //dont chain
            return;
        }
        castOn(event.getTarget(), (ISpigotCharacter) event.getCaster(), info, skillContext);
    }


    public static LivingEntity rayTraceEntity(final Player player, final double maxDistance) {
        if (maxDistance <= 0.0) {
            return null;
        }
        RayTraceResult rayTraceResult = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), maxDistance, FluidCollisionMode.NEVER, true,1, entity -> true);
        Entity hitEntity = rayTraceResult.getHitEntity();
        if (hitEntity != null) {
            if (hitEntity instanceof LivingEntity) {
                return (LivingEntity) hitEntity;
            }
        }
        return null;
    }
}
