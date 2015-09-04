package cz.neumimto.effects;

/**
 * Created by NeumimTo on 17.2.2015.
 */
public enum EffectSource {
    DEFAULT(false),
    RACE(false),
    GUILD(false),
    PASSIVE_SKILL(false),
    TEMP(true);

    private boolean clearOnDeath;

    EffectSource(boolean clearOnDeath) {
        this.clearOnDeath = clearOnDeath;
    }

    public boolean isClearOnDeath() {
        return clearOnDeath;
    }
}
