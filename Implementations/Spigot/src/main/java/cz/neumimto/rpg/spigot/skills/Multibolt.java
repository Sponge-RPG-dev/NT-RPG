package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@ResourceLoader.Skill("ntrpg:multibolt")
public class Multibolt extends TargetedEntitySkill {

    @Inject
    private SpigotDamageService spigotDamageService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DAMAGE, "level * 2 + 8");
        settings.addExpression(SkillNodes.PERIOD, "25");
        settings.addExpression("max-strikes", "3");
        addSkillType(SkillType.DAMAGE_CHECK_TARGET);
        addSkillType(SkillType.LIGHTNING);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        int maxStrikes = info.getIntNodeValue("max-strikes");
        if (maxStrikes <= 0) {
            return SkillResult.FAIL;
        }
        maxStrikes = ThreadLocalRandom.current().nextInt(maxStrikes - 1) + 1;

        int period = info.getIntNodeValue(SkillNodes.PERIOD);
        double damage = info.getDoubleNodeValue(SkillNodes.DAMAGE);

        new MultiboltRunnable(maxStrikes,
                (LivingEntity) target.getEntity(),
                source.getEntity(),
                damage)
                .runTaskTimer(SpigotRpgPlugin.getInstance(), 0, period);


        return SkillResult.OK;
    }

    private class MultiboltRunnable extends BukkitRunnable {

        int remainingStrikes;
        LivingEntity target;
        LivingEntity attacker;
        double damage;

        public MultiboltRunnable(int remainingStrikes, LivingEntity target, LivingEntity attacker, double damage) {
            this.remainingStrikes = remainingStrikes;
            this.target = target;
            this.attacker = attacker;
            this.damage = damage;
        }

        @Override
        public void run() {
            if (remainingStrikes == 0) {
                cancel();
                return;
            }
            remainingStrikes = remainingStrikes -1;

            if (spigotDamageService.damage(attacker, target, EntityDamageEvent.DamageCause.LIGHTNING, damage, false)) {
                target.getLocation().getWorld().strikeLightningEffect(target.getLocation());
            }
        }
    }
}
