package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;

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
        final int range = info.getIntNodeValue("range");
        final IEntity iEntity = TargettedEntity.get(range, character);
        ;
        //iEntity = TargettedEntity.get(range, character);
        if (Exists.test(iEntity)) {
            if (DamageEntity.damage(20, iEntity, character)) {
                delay(1000, () -> {
                    character.getAllowedArmor();
                    info.getBonusLevel();
                    iEntity.getEntity();
                    int k = range;
                });
                return SkillResult.OK;
            }
        }
        return SkillResult.CANCELLED;
    }
}
