package cz.neumimto.rpg.sponge.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.skills.scripting.*;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

import javax.inject.Singleton;

@Singleton
@TargetSelector("skill_damage")
public class SkillDamage {

    @Handler
    public void getTargets(@Caster ISpongeCharacter character, @Target IEntity target, @SkillArgument("settings.damage") float damage, ISkill skill) {
        SkillDamageSource s = new SkillDamageSourceBuilder()
                .fromSkill(skill)
                .setSource(character)
                .build();
        ((ISpongeEntity) target).getEntity().damage(damage, s);
    }
}
