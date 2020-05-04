package cz.neumimto.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.types.Targeted;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:empathy")
public class Empathy extends Targeted {

    @Inject
    private EntityService entityService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.MULTIPLIER, 5, 10);
        settings.addNode("max-damage", 100, 10);
        setDamageType(DamageTypes.MAGIC.getId());
    }

    @Override
    public SkillResult castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext skillContext) {
        Player entity = source.getEntity();
        Double max = entity.get(Keys.MAX_HEALTH).get();
        Double a = entity.get(Keys.HEALTH).get();
        a = max - a;
        a *= skillContext.getFloatNodeValue(SkillNodes.MULTIPLIER);
        max = skillContext.getDoubleNodeValue("max-damage");
        if (max > 0) {
            a = a < max ? max : a;
        }
        SkillDamageSource build = new SkillDamageSourceBuilder()
                .fromSkill(this)
                .setSource(source)
                .build();
        ((ISpongeEntity) target).getEntity().damage(a, build);
        return SkillResult.OK;
    }
}
