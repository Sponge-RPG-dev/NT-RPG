package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:megabolt")
public class SkillMegabolt extends ActiveSkill<ISpongeCharacter> {

    @Override
    public void init() {
        super.init();
        setDamageType(NDamageType.LIGHTNING.getId());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.RADIUS, 30, 5);
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.LIGHTNING);
    }

    @Override
    public SkillResult cast(ISpongeCharacter caster, PlayerSkillContext skillContext) {
        int r = skillContext.getIntNodeValue(SkillNodes.RADIUS);
        Set<Entity> nearbyEntities = Utils.getNearbyEntities(caster.getPlayer().getLocation(), r);
        float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
        SkillDamageSource s = new SkillDamageSourceBuilder()
                .fromSkill(this)
                .setSource(caster)
                .build();
        for (Entity e : nearbyEntities) {
            if (Utils.isLivingEntity(e)) {
                Living l = (Living) e;
                if (l.damage(damage, s)) {
                    Decorator.strikeLightning(l);
                }
            }
        }
        return SkillResult.OK;
    }
}
