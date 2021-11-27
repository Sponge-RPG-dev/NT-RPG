package cz.neumimto.rpg.common.effects.core;

import cz.neumimto.rpg.common.effects.CoreEffectTypes;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import java.lang.ref.WeakReference;

/**
 * Created by NeumimTo on 10.10.2015.
 */

public class CombatEffect extends EffectBase {

    public static final String name = "CombatTimer";
    private IActiveCharacter character;
    private WeakReference<IActiveCharacter> opponent;
    private long initiation;
    private final long combatTime;

    public CombatEffect(IActiveCharacter consumer, long combatTime) {
        super(name, consumer);
        this.character = consumer;
        opponent = new WeakReference<>(null);
        this.combatTime = combatTime;
        initiation = System.currentTimeMillis() - this.combatTime;
        effectTypes.add(CoreEffectTypes.COMBAT_TIMER);
    }

    public boolean isInCombat() {
        return initiation <= System.currentTimeMillis();
    }

    public IActiveCharacter getOpponent() {
        return opponent.get();
    }

    public void setOpponent(IActiveCharacter character) {
        initiation = System.currentTimeMillis() + this.combatTime;
        opponent = new WeakReference<>(character);
    }
}
