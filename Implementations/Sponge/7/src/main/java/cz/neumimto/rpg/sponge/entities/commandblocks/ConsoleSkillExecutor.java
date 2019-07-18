package cz.neumimto.rpg.sponge.entities.commandblocks;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.effects.EffectContainer;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.*;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.*;

/**
 * Created by NeumimTo on 1.8.2017.
 */
public class ConsoleSkillExecutor extends PreloadCharacter implements ISpongeEntity {


    private static IReservable INFINITY_POOL = new InfinityPool();
    private ArmorStand mock;
    private Map<String, IEffectContainer<Object, IEffect<Object>>> effectMap;

    private ConsoleSkillExecutor(Location<Extent> location, Vector3d headRotation) {
        super(null);
        mock = (ArmorStand) location.getExtent().createEntity(EntityTypes.ARMOR_STAND, location.getPosition());
        mock.offer(Keys.INVISIBLE, true);
        mock.offer(Keys.INVULNERABLE, true);
        mock.offer(Keys.HAS_GRAVITY, false);
        location.getExtent().spawnEntity(mock);
        mock.setHeadRotation(headRotation);
        mock.setRotation(headRotation);
        effectMap = new HashMap<>();
    }

    public static ConsoleSkillExecutor wrap(Location<Extent> location, Vector3d headRotation) {
        return new ConsoleSkillExecutor(location, headRotation);
    }

    @Override
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effectMap;
    }

    @Override
    public boolean isDetached() {
        return false;
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
    public Living getEntity() {
        return mock;
    }

    @Override
    public boolean hasEffect(String cl) {
        return effectMap.containsKey(cl);
    }

    @Override
    public boolean hasSkill(String name) {
        return true;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessage(int channel, String message) {

    }

    @Override
    public void sendNotification(String message) {

    }

    @Override
    public boolean hasCooldown(String thing) {
        return false;
    }

    @Override
    public UUID getUUID() {
        return UUID.randomUUID();
    }

    @Override
    public boolean isStub() {
        return false;
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
