package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.UnstackableEffectBase;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

@Generate(id = "name", description = "Stuns entity for a duration")
public class StunEffect extends UnstackableEffectBase<Long> {
    public static String name = "Stun";
    private LivingEntity livingEntity;
    private Location appliedLoc;


    public StunEffect(ISpigotEntity consumer, long duration) {
        super(name, consumer);
        setValue(duration);
        setDuration(duration);
        setPeriod(20);
        livingEntity = consumer.getEntity();
        appliedLoc = livingEntity.getLocation();
    }

    @Generate.Constructor
    public StunEffect(IEffectConsumer consumer, long duration) {
        this((ISpigotEntity) consumer, duration);
    }


    @Override
    public void onTick(IEffect self) {
        livingEntity.teleport(appliedLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
