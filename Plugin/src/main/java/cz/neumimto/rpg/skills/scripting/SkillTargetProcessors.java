package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.IEntityType;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.utils.SkillUtils;
import cz.neumimto.rpg.utils.Utils;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;

import java.util.*;

public class SkillTargetProcessors {

    public static final FindSkillTargetProcessor SELF = (caster, context) -> Collections.singletonList(caster);

    public static final FindSkillTargetProcessor NEARBY_ALLIES = ((caster, context) -> {
        float radius = context.getSkillInfo().getSkillData()
                .getSkillSettings()
                .getLevelNodeValue(SkillNodes.RADIUS, context.getSkillInfo().getTotalLevel());
        Collection<Entity> nearbyEntities = caster.getEntity().getNearbyEntities(radius);
        List<IEntity> nearby = new ArrayList<>();
        if (caster.getType() == IEntityType.MOB) {
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity.getType() == EntityTypes.PLAYER) {
                    continue;
                }
                if (!(nearbyEntity instanceof Living)) {
                    continue;
                }
                if (nearbyEntity.get(Keys.TAMED_OWNER).isPresent()) {
                    continue;
                }
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                nearby.add(iEntity);
            }
        } else if (caster.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) caster;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    nearby.add(iEntity);
                }
            }
        }
        return nearby;
    });

    public static final FindSkillTargetProcessor TARGETTED_ENEMY = (((caster, context) -> {
        if (caster.getType() == IEntityType.MOB) {

            Living entity = caster.getEntity();
            if (entity instanceof Monster) {
                Optional<Entity> target = ((Monster) entity).getTarget();
                if (target.isPresent()) {
                    Entity mtarget = target.get();
                    if (!(mtarget instanceof Living)) {
                        return Collections.emptyList();
                    }
                    return Collections.singletonList(NtRpgPlugin.GlobalScope.entityService.get(mtarget));
                } else {
                    return Collections.emptyList();
                }
            } else {
                float range = context.getSkillInfo().getSkillData()
                        .getSkillSettings()
                        .getLevelNodeValue(SkillNodes.RANGE, context.getSkillInfo().getTotalLevel());
                Collection<Entity> nearbyEntities = entity.getNearbyEntities(range);
                throw new NotImplementedException(":(");
            }
        } else if (caster.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) caster;
            int range = (int) context.getSkillInfo().getSkillData()
                    .getSkillSettings()
                    .getLevelNodeValue(SkillNodes.RANGE, context.getSkillInfo().getTotalLevel());
            Living targettedEntity = Utils.getTargettedEntity(character, range);
            if (targettedEntity != null) {
                return Collections.singletonList(NtRpgPlugin.GlobalScope.entityService.get(targettedEntity));
            }
        }
        return Collections.emptyList();
    }));


}
