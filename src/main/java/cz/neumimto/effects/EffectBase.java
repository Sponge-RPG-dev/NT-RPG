package cz.neumimto.effects;

import cz.neumimto.GlobalScope;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.utils.UUIDs;
import org.spongepowered.api.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo.
 */
public class EffectBase implements IEffect {
    private boolean stackable = false;
    private String name;
    private int level;
    private Set<PotionEffect> potions = new HashSet<>();
    private IEffectConsumer consumer;
    private EffectSource effectSource = EffectSource.TEMP;
    private long duration = -1;
    private long period = -1;
    private long lastTickTime;
    private long expireTime;
    private int tickCount;
    private long timeCreated;
    private String applyMessage;
    private String expireMessage;
    private UUID uuid;

    public EffectBase(String name, IEffectConsumer consumer) {
        this();
        this.name = name;
        this.consumer = consumer;
    }

    public EffectBase() {
        timeCreated = System.currentTimeMillis();
        uuid = UUIDs.random();
    }

    public void setConsumer(IEffectConsumer consumer) {
        if (consumer != null)
            this.consumer = consumer;
    }

    @Override
    public boolean requiresRegister() {
        return duration >= 0 && period >= 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply() {
        for (PotionEffect e : getPotions()) {
            getConsumer().addPotionEffect(e);
        }
    }

    @Override
    public void onRemove() {
        for (PotionEffect e : getPotions()) {
            getConsumer().removePotionEffect(e.getType());
        }
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    @Override
    public boolean setStackable(boolean b) {
        return stackable;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    public IEffectConsumer getConsumer() {
        return consumer;
    }

    @Override
    public EffectSource getEffectSource() {
        return effectSource;
    }

    @Override
    public void setEffectSource(EffectSource effectSource) {
        this.effectSource = effectSource;
    }

    public Set<PotionEffect> getPotions() {
        return potions;
    }

    @Override
    public long getExpireTime() {
        return timeCreated + duration;
    }

    @Override
    public long getTimeLeft(long currenttime) {
        return timeCreated + duration - currenttime;
    }

    @Override
    public void onStack(int level) {
        for (PotionEffect e : getPotions()) {
            getConsumer().addPotionEffect(e.getType(), e.getAmplifier(), e.getDuration());
        }
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long l) {
        this.duration = l;
    }

    @Override
    public void tickCountIncrement() {
        tickCount++;
    }

    protected int getTickCount() {
        return tickCount;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(long period) {
        this.period = period;
    }

    @Override
    public long getLastTickTime() {
        return lastTickTime;
    }

    @Override
    public void setLastTickTime(long lastTickTime) {
        this.lastTickTime = lastTickTime;
    }

    protected void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public void onTick() {

    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof EffectBase)) return false;

        EffectBase that = (EffectBase) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }


    @Override
    public String getExpireMessage() {
        return expireMessage;
    }

    @Override
    public void setExpireMessage(String expireMessage) {
        this.expireMessage = expireMessage;
    }

    @Override
    public String getApplyMessage() {
        return applyMessage;
    }

    @Override
    public void setApplyMessage(String applyMessage) {
        this.applyMessage = applyMessage;
    }

    public static GlobalScope getGlobalScope() {
        return NtRpgPlugin.GlobalScope;
    }
}
