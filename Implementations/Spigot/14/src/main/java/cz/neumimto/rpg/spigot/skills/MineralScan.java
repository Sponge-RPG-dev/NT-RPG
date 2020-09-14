package cz.neumimto.rpg.spigot.skills;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.utils.WrapperPlayServerEntityMetadata;
import cz.neumimto.rpg.spigot.skills.utils.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Singleton
@ResourceLoader.Skill("ntrpg:mineralscan")
public class MineralScan extends ActiveSkill<ISpigotCharacter> {

    private static int ID = Integer.MAX_VALUE;

    @Override
    public void init() {
        super.init();
        addSkillType(SkillType.ILLUSION);
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
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
                        spawnGlowingBlock(location, player);
                    }
                }
            }
        }
        return SkillResult.OK;
    }


    private void spawnGlowingBlock(Location location, Player player) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer entity = manager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        entity.getIntegers().write(0, ID).write(1, 38); //magmacube = 38

        entity.getUUIDs().write(0, UUID.randomUUID());
        entity.getDoubles().write(0, location.getX()).write(1, location.getY()).write(2, location.getZ());


        WrapperPlayServerSpawnEntityLiving living = new WrapperPlayServerSpawnEntityLiving(entity);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(ID);
        List<WrappedWatchableObject> list = new ArrayList<>();
        //  list.add(new WrappedWatchableObject());
        metadata.setMetadata(list);
        ID--;
        living.sendPacket(player);
        metadata.sendPacket(player);
    }


}

