package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.TargetedBlockSkill;
import de.slikey.effectlib.effect.CloudEffect;
import org.bukkit.block.Block;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:blizzard")
public class Blizzard extends TargetedBlockSkill {
    @Override
    protected void castOn(Block block, ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        CloudEffect cloudEffect = new CloudEffect(SpigotRpgPlugin.getEffectManager());
        cloudEffect.cloudSize = 15;
        cloudEffect.duration = 20000;
        SpigotRpgPlugin.getEffectManager().start(cloudEffect);
    }



}
