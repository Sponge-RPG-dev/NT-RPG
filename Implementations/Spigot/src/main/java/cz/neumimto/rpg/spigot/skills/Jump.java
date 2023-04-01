package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.inject.Singleton;
import java.util.EnumSet;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:jump")
public class Jump extends ActiveSkill<SpigotCharacter> {

    static EnumSet<Material> unstableMaterials;

    @Override
    public void init() {
        super.init();
        settings.addExpression("velocity-vertical", 2);
        settings.addExpression("velocity-horizontal", 2);

        addSkillType(SkillType.MOVEMENT);
    }

    @Override
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        Location playerLoc = player.getLocation();

        float multiplier = (90.0f + player.getEyeLocation().getPitch()) / 50.0f;


        double velVert = skillContext.getDoubleNodeValue("velocity-vertical");
        double velHor = skillContext.getDoubleNodeValue("velocity-horizontal");
        velVert = Math.min(velVert, 2.0);


        final Material steppedMaterial = playerLoc.getBlock().getRelative(BlockFace.DOWN).getType();
        if (unstableMaterials.contains(steppedMaterial)) {
            velVert *= 0.75;
            velHor *= 0.75;
        }
        Vector direction = player.getLocation().getDirection();
        Vector velocity = player.getVelocity().setY(velVert);

        direction.setY(0).normalize().multiply(multiplier);
        velocity.multiply(new Vector(velHor, 1.0, velHor));
        player.setVelocity(velocity);
        player.setFallDistance(-10.0f);
        player.getWorld().spawnParticle(Particle.BLOCK_DUST, player.getLocation(), 15, 0.0, 0.3, 0.0, steppedMaterial.data);
        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 15, 0.0, 0.1, 0.0, steppedMaterial.data);

        return SkillResult.OK;
    }

    static {
        unstableMaterials = EnumSet.noneOf(Material.class);
        unstableMaterials.add(Material.WATER);
        unstableMaterials.add(Material.LAVA);

        for (Material unstableMaterial : unstableMaterials) {
            if (unstableMaterial.toString().contains("_LEAVES")) {
                unstableMaterials.add(unstableMaterial);
            }
        }

        unstableMaterials.add(Material.SOUL_SAND);
        unstableMaterials.add(Material.CAMPFIRE);
        unstableMaterials.add(Material.CAKE);
    }
}
