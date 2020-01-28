package cz.neumimto.skills;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.Targeted;

public class Lightning extends Targeted {
    @Override
    public void castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info, SkillContext skillContext) {

    }
}
