package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:jump")
public class Jump extends ActiveSkill<ISpigotCharacter> {

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        addSkillType(SkillType.MOVEMENT);
    }

    @Override
    public void cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext modifier) {

    }
}
