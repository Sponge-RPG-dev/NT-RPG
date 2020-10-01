package cz.neumimto.rpg;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.common.skills.mech.TargetSelectorSelf;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import javax.inject.Inject;

@ResourceLoader.Skill("test2")
public class Test extends ActiveSkill<IActiveCharacter> {
    @Inject
    DamageMechanic damageMechanic;

    @Inject
    TargetSelectorSelf targetSelectorSelf;

    public SkillResult cast(final IActiveCharacter caster, final PlayerSkillContext context) {
        Object2FloatOpenHashMap<String> map = context.getCachedComputedSkillSettings();
        return SkillResult.OK;
    }
}