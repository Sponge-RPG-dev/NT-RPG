package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@AutoService(IEffect.class)
@ScriptMeta.Function("WebEffect")
public class WebEffect extends EffectBase<Long> {

    public static String name = "Web";
    private Vector[] vector3is = new Vector[9];
    private World world;

    @ScriptMeta.Handler
    public WebEffect(@ScriptMeta.NamedParam("e|entity") IEffectConsumer effectConsumer,
                     @ScriptMeta.NamedParam("d|duration") long duration) {
        super(name, effectConsumer);
        setDuration(duration);
        setStackable(false, null);

    }

    @Override
    public void onApply(IEffect self) {
        super.onApply(self);

        Location location = ((ISpigotEntity) getConsumer()).getEntity().getLocation();
        Vector vector1 = location.toVector();
        world = location.getWorld();

        int b = 0;
        for (int i = -1; i <= 1; i++) {
            for (int x = -1; x <= 1; x++) {
                Vector vector = new Vector(vector1.getX() + i, vector1.getY(), vector1.getZ() + x);
                vector3is[b] = vector;
                b++;

                Block blockAt = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                if (blockAt.getType() == Material.AIR) {
                    blockAt.setType(Material.COBWEB);
                }
            }
        }
    }

    @Override
    public void onRemove(IEffect self) {
        super.onRemove(self);

        for (Vector vector3i : vector3is) {
            Block block = world.getBlockAt(vector3i.getBlockX(), vector3i.getBlockY(), vector3i.getBlockZ());
            if (block.getType() == Material.COBWEB) {
                block.setType(Material.AIR);
            }
        }
    }
}
