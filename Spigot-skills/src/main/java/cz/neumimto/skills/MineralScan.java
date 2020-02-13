package cz.neumimto.skills;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.inject.Singleton;


@Singleton
@ResourceLoader.Skill("ntrpg:mineralscan")
public class MineralScan extends ActiveSkill<ISpigotCharacter> {

    private ProtocolManager protocolManager;

    @Override
    public void init() {
        super.init();
        addSkillType(SkillType.ILLUSION);
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        Player player = character.getPlayer();
        World world = player.getWorld();
        Location location = player.getLocation();
        for (int i = -3; i < 3; i++) {
            for (int j = -3; j < 3; j++) {
                for (int k = -3; k < 3; k++) {
                    Location loc = location.clone().add(i, j, k);
                    Block blockAt = world.getBlockAt(loc);
                    Material material = blockAt.getBlockData().getMaterial();
                    DyeColor color = null;
                    switch (material) {
                        case GOLD_ORE:
                            color = DyeColor.YELLOW;
                            break;
                        case IRON_ORE:
                            color = DyeColor.WHITE;
                            break;
                        case DIAMOND_ORE:
                            color = DyeColor.LIGHT_BLUE;
                            break;
                        case EMERALD_ORE:
                            color = DyeColor.GREEN;
                            break;
                    }
                    if (color != null) {
                        Shulker entity = (Shulker) world.spawnEntity(loc, EntityType.SHULKER);
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1));
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1));
                    }
                }
            }
        }
    }




}

