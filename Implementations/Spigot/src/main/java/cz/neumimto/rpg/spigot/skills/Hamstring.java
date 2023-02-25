package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.effects.common.Maim;
import cz.neumimto.rpg.spigot.effects.common.model.SlowModel;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:hamstring")
public class Hamstring extends TargetedEntitySkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        setDamageType(EntityDamageEvent.DamageCause.CONTACT.name());
        addSkillType(SkillType.PHYSICAL);
        settings.addNode("weapon-damage-mult", 105);
        settings.addNode(SkillNodes.DURATION, 5000);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext skillContext) {
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        int i = skillContext.getIntNodeValue(SkillNodes.AMPLIFIER);
        SlowModel slowModel = new SlowModel();
        slowModel.slowLevel = i;
        slowModel.decreasedJumpHeight = true;
        Maim effect = new Maim(target, duration, slowModel);
        effectService.addEffect(effect, this);

        double k = skillContext.getDoubleNodeValue("weapon-damage-mult");
        if (k > 0) {
            double damage = 1 * k;
            LivingEntity entity = (LivingEntity) target.getEntity();
            entity.damage(damage, source.getEntity());
        }

        return SkillResult.OK;
    }
}
