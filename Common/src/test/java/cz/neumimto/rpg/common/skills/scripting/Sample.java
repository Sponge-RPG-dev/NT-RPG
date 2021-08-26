package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

import javax.inject.Inject;
/*
    @target = targetted_entity{range=$settings.range, entityFrom=@caster}
    IF exists{test=@target}
       IF damage_entity{damage=20, damaged=@target, damager=@caster}
          DELAY 1000
            spawn_lighting{location=@target}
          END
          RETURN OK
       END
    END
    RETURN CANCELLED
 */
public class Sample extends ActiveSkill {
    @Inject
    public ScríptParserTests.TargettedEntity TargettedEntity;
    @Inject
    public ScríptParserTests.Exists Exists;
    @Inject
    public ScríptParserTests.DamageEntity DamageEntity;
    @Inject
    public ScríptParserTests.A A;

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        int range = info.getIntNodeValue("range");

        IEntity iEntity = TargettedEntity.get(range, character);
        if (Exists.test(iEntity)) {
            if (DamageEntity.damage(20, iEntity, character)) {
                delay(1000, () -> {
                    A.spawn(iEntity);
                });
                return SkillResult.OK;
            }
        }
        return SkillResult.CANCELLED;
    }
}
