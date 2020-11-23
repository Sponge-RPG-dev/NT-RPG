package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:fireblast")
public class FireBlast extends TargetedBlockSkill {

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected SkillResult castOn(Block block, ISpigotCharacter character, PlayerSkillContext skillContext) {

        spawnFireworks(block.getLocation());

        return SkillResult.OK;
    }

    private void spawnFireworks(Location location){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(0);
        fwm.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(255,0,0))
                .flicker(true)
                .build());
        fwm.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(255, 102, 0))
                .build());

        fw.setFireworkMeta(fwm);
        fw.detonate();
    }
}
