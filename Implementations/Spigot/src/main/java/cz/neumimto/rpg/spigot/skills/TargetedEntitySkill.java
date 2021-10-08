package cz.neumimto.rpg.spigot.skills;


import com.google.inject.Inject;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.events.skill.SkillTargetAttemptEvent;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.types.ITargeted;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.scripting.mechanics.Targetting;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public abstract class TargetedEntitySkill extends ActiveSkill<ISpigotCharacter> implements ITargeted<ISpigotCharacter> {

    @Inject
    protected DamageService<IActiveCharacter, Object, IEntity<Object>> damageService;

    @Inject
    protected Targetting targettedEntity;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.RANGE, 10);
    }

    @Override
    public SkillResult cast(ISpigotCharacter caster, PlayerSkillContext skillContext) {
        int range = skillContext.getIntNodeValue(SkillNodes.RANGE);
        LivingEntity l = rayTraceEntity(caster.getPlayer(), range);
        if (l == null) {
            if (getDamageType() == null && !getSkillTypes().contains(SkillType.CANNOT_BE_SELF_CASTED)) {
                l = caster.getEntity();
            } else {
                return SkillResult.NO_TARGET;
            }
        }
        if ((getDamageType() != null || getSkillTypes().contains(SkillType.DAMAGE_CHECK_TARGET)) && !damageService.canDamage(caster, l)) {
            return SkillResult.CANCELLED;
        }
        IEntity<LivingEntity> target = Rpg.get().getEntityService().get(l);

        SkillTargetAttemptEvent event = Rpg.get().getEventFactory().createEventInstance(SkillTargetAttemptEvent.class);
        event.setSkill(this);
        event.setCaster(caster);
        event.setTarget(target);


        if (Rpg.get().postEvent(event)) {
            //todo https://github.com/Sponge-RPG-dev/NT-RPG/issues/111
            return SkillResult.CANCELLED;
        }
        return castOn(event.getTarget(), (ISpigotCharacter) event.getCaster(), skillContext);
    }


    public static LivingEntity rayTraceEntity(final Player player, final double maxDistance) {
        if (maxDistance <= 0.0) {
            return null;
        }
        RayTraceResult rayTraceResult = player.getWorld()
                .rayTraceEntities(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        maxDistance,
                        entity -> entity != player);
        if (rayTraceResult != null) {
            Entity hitEntity = rayTraceResult.getHitEntity();
            if (hitEntity != null) {
                if (hitEntity instanceof LivingEntity) {
                    return (LivingEntity) hitEntity;
                }
            }
        }
        return null;
    }
}
