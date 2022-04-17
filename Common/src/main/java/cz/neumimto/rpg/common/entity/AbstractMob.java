package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMob<T> implements IMob<T> {

    protected double experiences;
    protected Map<String, IEffectContainer<Object, IEffect<Object>>> effectSet;
    protected Map<Integer, Double> properties;

    public AbstractMob() {
        properties = new HashMap<Integer, Double>();
        effectSet = new HashMap<>();
    }

    @Override
    public double getExperiences() {
        return experiences;
    }

    @Override
    public void setExperiences(double experiences) {
        this.experiences = experiences;
    }

    @Override
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effectSet;
    }

    @Override
    public double getProperty(int propertyName) {
        if (properties.containsKey(propertyName)) {
            return properties.get(propertyName);
        }
        return Rpg.get().getPropertyService().getDefault(propertyName);
    }


    @Override
    public void setProperty(int propertyName, double value) {
        properties.put(propertyName, value);
    }

    @Override
    public boolean isDetached() {
        return getEntity() == null;
    }
}
