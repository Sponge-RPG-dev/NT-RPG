package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import javax.inject.Inject;

public class Test extends ActiveSkill {

    @Inject
    private DamageMechanic DamageMechanic;

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        Object2FloatOpenHashMap<String> settings = info.getCachedComputedSkillSettings();
        float damage = settings.getFloat("damage");
        float n = settings.getFloat("n");
        DamageMechanic.damage(character, character, damage);
        
        return SkillResult.OK;
    }
}
