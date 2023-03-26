package cz.neumimto.rpg.nms119;

import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

public class CustomFirework extends FireworkRocketEntity {

    List<Player> players = null;
    boolean flag = false;

    public CustomFirework(Location location, FireworkEffect effect, List<Player> p) {
        super(EntityType.FIREWORK_ROCKET, ((CraftWorld) location.getWorld()).getHandle());
        players = p;
        Firework bukkitEntity = (Firework) getBukkitEntity();
        FireworkMeta meta = bukkitEntity.getFireworkMeta();
        meta.addEffect(effect);
        bukkitEntity.setFireworkMeta(meta);
        setOrigin(location);

        if ((((CraftWorld) location.getWorld()).getHandle()).addFreshEntity(this, CreatureSpawnEvent.SpawnReason.COMMAND)) {
            setInvisible(true);
        }
    }

    @Override
    public void tick() {
        if (flag) {
            return;
        }
        flag = true;
        if (players != null) {
            if (players.size() > 0) {

                for (Player player : players) {
                    (((CraftPlayer) player).getHandle()).connection.send(new ClientboundEntityEventPacket(this, (byte) 17));
                }
                this.discard();
                return;
            }
        }
        level.broadcastEntityEvent(this, (byte) 17);
        this.discard();
    }
}
