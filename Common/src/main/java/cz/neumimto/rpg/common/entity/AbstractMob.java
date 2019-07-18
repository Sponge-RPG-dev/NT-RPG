package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEntityResource;
import cz.neumimto.rpg.api.entity.IMob;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMob<T> implements IMob<T> {

    protected double experiences;
    protected Map<String, IEffectContainer<Object, IEffect<Object>>> effectSet;
    protected Map<Integer, Float> properties;
    protected IEntityResource entityHealth;

    public AbstractMob() {
        properties = new HashMap<>();
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
    public IEntityResource getHealth() {
        return entityHealth;
    }

    @Override
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effectSet;
    }

    @Override
    public float getProperty(int propertyName) {
        if (properties.containsKey(propertyName)) {
            return properties.get(propertyName);
        }
        return Rpg.get().getPropertyService().getDefault(propertyName);
    }


    @Override
    public void setProperty(int propertyName, float value) {
        properties.put(propertyName, value);
    }

    @Override
    public boolean isDetached() {
        return getEntity() == null;
    }
}
