package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;

@ClassGenerator.Generate(id = "name")
public class LifeAfterKillEffect extends EffectBase {

    public static final String name = "Life after each kill";
    private IActiveCharacter character;
    private float healedAmount;
    public LifeAfterKillEffect(IActiveCharacter character, long duration, float level) {
        this.character = character;
        setDuration(duration);
        this.healedAmount = level;
        setStackable(false);
    }

    public float getHealedAmount() {
        return healedAmount;
    }

    public void setHealedAmount(float healedAmount) {
        this.healedAmount = healedAmount;
    }

}
