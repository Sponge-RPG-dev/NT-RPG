package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.ToggleableSkill;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:levitate")
public class Levitate extends ToggleableSkill<ISpigotCharacter> {

    @Override
    public String getEffectName() {
        return "";
    }

    @Override
    public IEffect constructEffect(ISpigotCharacter character, PlayerSkillContext info) {
        return null;
    }
}
