package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.common.skills.mech.NearbyEnemies;
import cz.neumimto.rpg.common.skills.mech.TargetSelectorSelf;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import javax.inject.Inject;

public class Test extends ActiveSkill {

    @Inject
    private DamageMechanic DamageMechanic;

    @Inject
    private TargetSelectorSelf targetSelectorSelf;

    @Inject
    private NearbyEnemies nearbyEnemies;

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        Object2FloatOpenHashMap<String> settings = info.getCachedComputedSkillSettings();
        float damage = settings.getFloat("damage");
        IEntity target = targetSelectorSelf.getTarget(character);
        DamageMechanic.damage(character, target, damage);
        return SkillResult.OK;
    }
}
