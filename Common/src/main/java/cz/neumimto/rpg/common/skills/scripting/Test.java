package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.mech.Condition;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.common.skills.mech.NearbyEnemies;
import cz.neumimto.rpg.common.skills.mech.TargetSelectorSelf;

import javax.inject.Inject;
import java.util.List;

public class Test extends ActiveSkill {

    @Inject
    private DamageMechanic DamageMechanic;

    @Inject
    private TargetSelectorSelf targetSelectorSelf;

    @Inject
    private NearbyEnemies nearbyEnemies;

    @Inject
    private Condition boolChecker;

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {

        if (boolChecker.check(character, character) && boolChecker.check2(character, character)) {
            List<IEntity> targets = nearbyEnemies.getTargets(character, 20f);
            for (IEntity target : targets) {

            }
        }

        String k = "aaa";

        return SkillResult.OK;
    }
}
