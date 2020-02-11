package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.slikey.effectlib.effect.CloudEffect;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:blizzard")
public class Blizzard extends ActiveSkill<ISpigotCharacter> {

    @Override
    public void init() {

    }

    @Override
    public void cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        CloudEffect cloudEffect = new CloudEffect(SpigotRpgPlugin.getEffectManager());
        cloudEffect.cloudSize = 15;
        cloudEffect.duration = 20000;
        cloudEffect.setLocation(character.getEntity().getLocation());
        SpigotRpgPlugin.getEffectManager().start(cloudEffect);
    }
}
