package cz.neumimto.rpg.effects.common.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.*;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 23.12.2015.
 */

@ClassGenerator.Generate(id = "name", inject = false)
public class Invisibility extends EffectBase implements IEffectContainer {

    public static String name = "Invisibility";

    public Invisibility(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
    }

    public Invisibility(IEffectConsumer consumer, long duration, String level) {
        this(consumer, duration);
    }

    @Override
    public void onApply() {
        Living entity = getConsumer().getEntity();
        entity.offer(Keys.INVISIBLE, true);
    }

    @Override
    public void onRemove() {
        Living entity = getConsumer().getEntity();
        entity.offer(Keys.INVISIBLE, false);
    }

    @Override
    public IEffectContainer constructEffectContainer() {
        return this;
    }

    @Override
    public Set<Invisibility> getEffects() {
        return Collections.singleton(this);
    }

    @Override
    public Object getStackedValue() {
        return null;
    }

    @Override
    public void setStackedValue(Object o) {

    }
}
