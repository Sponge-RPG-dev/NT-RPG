package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;

import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:iceaura")
public class IceAura extends ActiveSkill<SpigotCharacter> {

    @Override
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext info) {
        // new IceAuraEffect(character,)
        return null;
    }
}
