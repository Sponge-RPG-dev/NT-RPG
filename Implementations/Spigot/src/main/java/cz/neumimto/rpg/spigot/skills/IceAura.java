package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:iceaura")
public class IceAura extends ActiveSkill<ISpigotCharacter> {

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info) {
        return null;
    }
}
