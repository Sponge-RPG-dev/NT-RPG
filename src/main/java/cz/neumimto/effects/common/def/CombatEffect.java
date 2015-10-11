package cz.neumimto.effects.common.def;

import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.IEffect;
import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.players.IActiveCharacter;

import java.lang.ref.WeakReference;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class CombatEffect extends EffectBase {

    private IActiveCharacter character;
    private WeakReference<IActiveCharacter> opponent;
    private long initiation;
    public static final String name = "CombatTimer";
    public CombatEffect(IActiveCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        opponent = new WeakReference<IActiveCharacter>(null);
        initiation = System.currentTimeMillis() - PluginConfig.COMBAT_TIME;
    }

    public boolean isInCombat() {
        return initiation <= System.currentTimeMillis();
    }

    public IActiveCharacter getOpponent() {
        return opponent.get();
    }

    public void setOpponent(IActiveCharacter character) {
        initiation = System.currentTimeMillis() + PluginConfig.COMBAT_TIME;
        opponent = new WeakReference<>(character);
    }
}
