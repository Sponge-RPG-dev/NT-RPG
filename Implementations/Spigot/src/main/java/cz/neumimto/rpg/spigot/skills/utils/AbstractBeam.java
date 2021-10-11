package cz.neumimto.rpg.spigot.skills.utils;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public abstract class AbstractBeam<T> extends BukkitRunnable {

    private int rayTraceTicks;
    private double maxDistance;
    private Location currentLoc;

    private LivingEntity entity;
    private World world;
    private int tick;
    private Vector vector;
    private Location initialLoc;
    private IEntity<LivingEntity> iEntity;
    //    private Async lance;
    private T data;
    private boolean stop;

    public void init(IEntity<LivingEntity> entity, double maxDistance, int rayTraceTicks) {
        this.iEntity = entity;
        this.entity = entity.getEntity();
        this.world = this.entity.getWorld();
        this.initialLoc = this.entity.getEyeLocation();
        this.currentLoc = initialLoc.clone();
        this.vector = this.entity.getEyeLocation().getDirection();
        this.maxDistance = maxDistance * maxDistance;
        this.rayTraceTicks = rayTraceTicks == 0 ? 1 : rayTraceTicks;
//        this.lance = new Async(entity.getEyeLocation().getDirection(), world, initialLoc.clone(), particleDensty);
    }

    public void start(long delay, long period) {
        runTaskTimer(SpigotRpgPlugin.getInstance(), delay, period);
    }

    @Override
    public void run() {
        if (!stop) {
            currentLoc.add(vector);
            if (tick % rayTraceTicks == 0) {
                RayTraceResult rayTraceResult = world
                        .rayTrace(
                                currentLoc,
                                vector,
                                vector.lengthSquared(),
                                FluidCollisionMode.NEVER,
                                true,
                                1,
                                entity -> entity != this.entity && entity instanceof LivingEntity && !entity.isDead());
                onTick(currentLoc, data, tick);
                if (rayTraceResult != null) {
                    LivingEntity hitEntity = (LivingEntity) rayTraceResult.getHitEntity();
                    if (hitEntity != null) {
                        IEntity iEntity = Rpg.get().getEntityService().get(hitEntity);
                        if (onEntityHit(this.iEntity, iEntity, data, tick)) {
                            cancel();
                            stop = true;
                        }
                    }
                    Block hitBlock = rayTraceResult.getHitBlock();
                    if (hitBlock != null) {
                        if (onBlockHit(hitBlock, data, tick)) {
                            cancel();
                            stop = true;
                        }
                    }
                }
            }
            tick++;

            if (initialLoc.distanceSquared(currentLoc) > maxDistance) {
                cancel();
                stop = true;
            }
        }
    }

    protected abstract void onTick(Location currentLoc, T data, int tick);

    protected abstract boolean onBlockHit(Block hitEntity, T data, int tick);

    protected abstract boolean onEntityHit(IEntity<LivingEntity> caster, IEntity<LivingEntity> hitEntity, T data, int tick);

//    public static class Async extends BukkitRunnable {
//
//        private final Vector vector;
//        private final World world;
//        private final Location location;
//        private final double particleDensity;
//
//        public Async(Vector clone, World world, Location initialLoc, double particleDensity) {
//            this.vector = clone;
//            this.world = world;
//            this.location = initialLoc;
//            this.particleDensity = particleDensity;
//        }
//
//        @Override
//        public void run() {
//            world.spawnParticle(Particle.CLOUD, location, 10);
//            location.add(vector);
//        }
//
//        @Override
//
//        public synchronized void cancel() throws IllegalStateException {
//            super.cancel();
//            System.out.println("Async Cancel");
//        }
//
//    }

//    public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long period) throws IllegalArgumentException, IllegalStateException {
//        lance.runTaskTimerAsynchronously(plugin, 0L, period);
//        return super.runTaskTimer(plugin, 0L, period);
//    }


//    @Override
//    public synchronized void cancel() throws IllegalStateException {
//        if (lance != null) {
//            lance.cancel();
//        }
//        super.cancel();
//    }


    public int getTick() {
        return tick;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
