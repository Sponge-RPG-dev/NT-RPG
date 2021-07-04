package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:icewall")
public class IceWall extends TargetedBlockSkill {

    @Override
    public void init() {
        super.init();

        settings.addExpression(SkillNodes.VELOCITY, "1.1");
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.ICE);

    }

    @Override
    protected SkillResult castOn(Block block, ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        Location eyeLocation = player.getEyeLocation();
        Location centerLocation = block.getLocation().clone().add(new Vector(0, 1, 0));

        Location rotated = eyeLocation.clone();
        rotated.setPitch(0);
        rotated.setYaw(eyeLocation.getYaw() - 90);
        Vector rotation = rotated.getDirection();
        // Location blockLocation = center.clone().add(rotation).subtract(0, amount / 2, 0);
        return SkillResult.OK;
    }

}
