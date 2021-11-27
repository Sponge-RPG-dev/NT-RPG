package cz.neumimto.rpg;

import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.common.skills.mech.TargetSelectorSelf;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import javax.inject.Inject;

@ResourceLoader.Skill("test2")
public class Test extends ActiveSkill<IActiveCharacter> {
    @Inject
    DamageMechanic damageMechanic;

    @Inject
    TargetSelectorSelf targetSelectorSelf;

    public SkillResult cast(final IActiveCharacter caster, final PlayerSkillContext context) {
        Object2DoubleOpenHashMap<String> map = context.getCachedComputedSkillSettings();
        return SkillResult.OK;
    }
}