package cz.neumimto.effects;

import cz.neumimto.GlobalScope;
import cz.neumimto.NtRpgPlugin;

import java.util.UUID;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffect {
    String getName();

    void onApply();

    void onStack(int level);

    void onRemove();

    int getLevel();

    void setLevel(int level);

    boolean isStackable();

    boolean setStackable(boolean b);

    boolean requiresRegister();

    EffectSource getEffectSource();

    void setEffectSource(EffectSource effectSource);

    long getPeriod();

    void setPeriod(long period);

    long getLastTickTime();

    void setLastTickTime(long currTime);

    void onTick();

    long getExpireTime();

    long getTimeLeft(long currenttime);

    long getDuration();

    void setDuration(long l);

    void tickCountIncrement();

    UUID getUUID();

    String getExpireMessage();

    void setExpireMessage(String expireMessage);

    String getApplyMessage();

    void setApplyMessage(String applyMessage);

    public static GlobalScope getGlobalScope(){
        return NtRpgPlugin.GlobalScope;
    }

}
