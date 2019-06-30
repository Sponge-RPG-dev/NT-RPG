package cz.neumimto.rpg.sponge.effects.common.def;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

import java.lang.ref.WeakReference;

/**
 * Created by NeumimTo on 10.10.2015.
 */

public class CombatEffect extends EffectBase {

    public static final String name = "CombatTimer";
    private IActiveCharacter character;
    private WeakReference<IActiveCharacter> opponent;
    private long initiation;

    public CombatEffect(ISpongeCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        opponent = new WeakReference<>(null);
        initiation = System.currentTimeMillis() - pluginConfig.COMBAT_TIME;
        effectTypes.add(CoreEffectTypes.COMBAT_TIMER);
    }

    public boolean isInCombat() {
        return initiation <= System.currentTimeMillis();
    }

    public IActiveCharacter getOpponent() {
        return opponent.get();
    }

    public void setOpponent(IActiveCharacter character) {
        initiation = System.currentTimeMillis() + pluginConfig.COMBAT_TIME;
        opponent = new WeakReference<>(character);
    }
}
