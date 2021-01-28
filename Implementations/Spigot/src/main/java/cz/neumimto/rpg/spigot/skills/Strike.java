package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.spigot.effects.common.BleedingEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Inject;
import java.util.concurrent.ThreadLocalRandom;

@ResourceLoader.Skill("ntrpg:strike")
public class Strike extends TargetedEntitySkill {

    @Inject
    private DamageService damageService;

    @Inject
    private EffectService effectService;

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        double weaponDamageMult = info.getDoubleNodeValue(SkillNodes.MULTIPLIER);
        double max = info.getDoubleNodeValue(SkillNodes.MAX);

        double damage = Math.min(max, weaponDamageMult * source.getWeaponDamage());

        if (damage > 0) {
            damageService.damageEntity(target, damage);
        }

        double bleedingDamage = info.getDoubleNodeValue("bleed-damage");
        int bleedingChance = info.getIntNodeValue("bleed-chance");
        if (bleedingDamage > 0 && bleedingChance > 0) {
            int i = ThreadLocalRandom.current().nextInt(100);
            if (bleedingChance > i) {
                long bleedingDuration = info.getLongNodeValue("bleed-duration");
                long bleedingPeriod = info.getLongNodeValue("bleed-period");
                BleedingEffect bleedingEffect = new BleedingEffect(target, bleedingDuration, bleedingPeriod, bleedingDamage);
                effectService.addEffect(bleedingEffect, this);
            }
        }

        return SkillResult.OK;
    }

}
