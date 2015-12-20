package cz.neumimto.entities;

import cz.neumimto.effects.IEffect;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Creature;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public class NEntity implements IMob {

    private double experiences;
    private Creature entity;
    private Map<Class<? extends IEffect>,IEffect> effectSet = new HashMap<>();

    protected NEntity(Creature l) {
        attach(l);
    }

    protected NEntity() {

    }

    @Override
    public double getExperiences() {
        return experiences;
    }

    @Override
    public void setExperiences(double exp) {
        this.experiences= exp;
    }

    @Override
    public double getHp() {
        return entity.get(Keys.HEALTH).get();
    }

    @Override
    public void setHp(double d) {
        entity.offer(Keys.HEALTH,d);
    }

    @Override
    public void attach(Creature creature) {
        this.entity = creature;
    }

    @Override
    public void detach() {
        this.entity = null;
    }

    @Override
    public boolean isDetached() {
        return entity != null;
    }

    @Override
    public Creature getEntity() {
        return entity;
    }

    @Override
    public Map<Class<? extends IEffect>, IEffect> getEffectMap() {
        return effectSet;
    }

    @Override
    public void sendMessage(String message) {

    }
}
