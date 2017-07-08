package cz.neumimto.skills.active;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;

import java.util.Set;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@ResourceLoader.Skill
public class SkillMegabolt extends ActiveSkill {

    public SkillMegabolt() {
        setName("Megabolt");
        setDamageType(NDamageType.LIGHTNING);
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.RADIUS, 30, 5);
        super.settings = settings;
        setDamageType(NDamageType.LIGHTNING);
    }

    @Override
    public SkillResult cast(IActiveCharacter iActiveCharacter, ExtendedSkillInfo extendedSkillInfo,SkillModifier skillModifier) {
        int r = (int) settings.getLevelNodeValue(SkillNodes.RADIUS,extendedSkillInfo.getTotalLevel());
        Set<Entity> nearbyEntities = Utils.getNearbyEntities(iActiveCharacter.getPlayer().getLocation(), r);
        float damage = settings.getLevelNodeValue(SkillNodes.DAMAGE,extendedSkillInfo.getTotalLevel());
        SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
        builder.fromSkill(this);
        builder.setCaster(iActiveCharacter);
        SkillDamageSource src = builder.build();
        for (Entity e : nearbyEntities) {
            if (Utils.isLivingEntity(e)) {
                Living l = (Living) e;
                if (Utils.canDamage(iActiveCharacter, l)) {
                    l.damage(damage,src);
                    Entity li = l.getLocation().getExtent().createEntity(EntityTypes.LIGHTNING, l.getLocation().getPosition());
                    l.getLocation().getExtent().spawnEntity(li, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
                }
            }
        }
        return SkillResult.OK;
    }
}
