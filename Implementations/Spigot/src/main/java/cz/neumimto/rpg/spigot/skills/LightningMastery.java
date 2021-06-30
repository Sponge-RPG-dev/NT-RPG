package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;

@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:lightningmastery")
public class LightningMastery extends PassiveSkill {

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {

    }
}
