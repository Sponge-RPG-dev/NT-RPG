package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.skills.scripting.JsBinding;
import cz.neumimto.rpg.spigot.utils.VectorUtils;
import de.slikey.effectlib.Effect;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Set;

@ScriptMeta.Function("ManaShieldEffect")
@AutoService(IEffect.class)
@Generate(id = "name", description = "Damage to mana")
public class ManaShieldEffect extends EffectBase<Double> implements IEffectContainer<Double, ManaShieldEffect> {

    public static final String name = "Mana Shield";
    private final double multiplier;
    private Effect effect;
    private LivingEntity consumer;
    private int tickCount;

    public static Vector[] circle;
    public static Particle.DustOptions dustOptions;

    static {
        circle = VectorUtils.circle(new Vector[30], 2);
        dustOptions = new Particle.DustOptions(Color.fromRGB(66, 120, 245), 1);
    }

    @ScriptMeta.Handler
    public ManaShieldEffect(@ScriptMeta.NamedParam("e|entity") IEffectConsumer consumer,
                            @ScriptMeta.NamedParam("d|duration") long duration,
                            @ScriptMeta.NamedParam("m|multiplier") double multiplier) {
        super(name, consumer);
        setDuration(duration);
        setPeriod(20);
        consumer = (IEffectConsumer) consumer.getEntity();
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void onTick(IEffect self) {
        tickCount++;
        display(tickCount);
    }

    private void display(int count) {
        if (count <= 0 || count >= circle.length) {
            count = 0;
        }

        consumer.getLocation().getWorld().spawnParticle(
                Particle.BLOCK_DUST,
                consumer.getLocation().add(circle[count]),
                1,
                dustOptions
        );
    }

    @Override
    public IEffectContainer constructEffectContainer() {
        return new EffectContainer.UnstackableSingleInstance(this);
    }

    @Override
    public Set<ManaShieldEffect> getEffects() {
        return Collections.singleton(this);
    }

    @Override
    public Double getStackedValue() {
        return getValue();
    }

    @Override
    public void setStackedValue(Double aDouble) {

    }
}
