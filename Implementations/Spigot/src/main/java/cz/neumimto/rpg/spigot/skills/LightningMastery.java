package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.types.PassiveSkill;

@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:lightningmastery")
public class LightningMastery extends PassiveSkill {

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {

    }
}
