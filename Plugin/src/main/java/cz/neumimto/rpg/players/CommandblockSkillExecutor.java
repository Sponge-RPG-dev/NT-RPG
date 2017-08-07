package cz.neumimto.rpg.players;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableCustomNameVisibleData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Created by NeumimTo on 1.8.2017.
 */
public class CommandblockSkillExecutor extends PreloadCharacter {
    private CommandBlock c;
    private static IReservable INFINITY_POOL = new InfinityPool();
    private CommandblockSkillExecutor(CommandBlock cb) {
        super(null);
        this.c = cb;
    }

    public static CommandblockSkillExecutor wrap(CommandBlock commandBlock) {
        return new CommandblockSkillExecutor(commandBlock);
    }

    @Override
    public Player getEntity() {
        throw new RuntimeException("CommandblockSkillExecutor.getEntity()");
    }

    @Override
    public IReservable getMana() {
        return INFINITY_POOL;
    }

    @Override
    public IReservable getHealth() {
        return INFINITY_POOL;
    }



    @Override
    public Location<World> getLocation() {
        return c.getLocation();
    }

    @Override
    public boolean hasEffect(String cl) {
        return true;
    }

    @Override
    public boolean hasSkill(String name) {
        return true;
    }

    @Override
    public boolean hasCooldown(String thing) {
        return false;
    }

    @Override
    public Vector3d getRotation() {
        return new Vector3d(0,c.getBlock().get(Keys.ROTATION).get().getAngle(),0);
    }

    private static class InfinityPool implements IReservable {


        @Override
        public double getMaxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public void setMaxValue(double f) {

        }

        @Override
        public double getRegen() {
            return 0;
        }

        @Override
        public void setRegen(float f) {

        }

        @Override
        public void setReservedAmnout(float f) {

        }

        @Override
        public double getReservedAmount() {
            return 0;
        }

        @Override
        public double getValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public void setValue(double f) {

        }
    }
}
