package cz.neumimto.rpg;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import java.util.List;

@ResourceLoader.Skill("test")
@Singleton
public class SkillExampleGen extends ActiveSkill {

    @Inject
    private cz.neumimto.rpg.common.skills.mech.DamageMechanic DamageMechanic;
    @Inject
    private cz.neumimto.rpg.common.skills.mech.NearbyEnemies NearbyEnemies;
    @Inject
    private cz.neumimto.rpg.common.skills.mech.ApplyEffect applyEffect;

    public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
        Object2FloatOpenHashMap<String> settings = info.getCachedComputedSkillSettings();
        float range = settings.getFloat("range");
        float damage = settings.getFloat("damage");

        List<IEntity> targets = NearbyEnemies.getTargets(character, range);
        for (IEntity iEntity : targets) {

          //  TickableEffect effect = new TickableEffect("test", iEntity, effectDuration0, effectDuration1);
            EffectService effectService;
          //  effectService.addEffect(effect, this, character);
        }

        return SkillResult.OK;
    }

}
