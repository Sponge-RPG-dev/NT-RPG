package cz.neumimto.effects.common.positive;

import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public class DamageBonus extends EffectBase {

    public static final String name = "BonusDamage";
    float bonusDamage;

    public DamageBonus(IEffectConsumer consumer, long duration, float bonusDamage) {
        super(name, consumer);
        setDuration(duration);
        setBonusDamage(bonusDamage);
    }

    public float getBonusDamage() {
        return bonusDamage;
    }

    public void setBonusDamage(float bonusDamage) {
        this.bonusDamage = bonusDamage;
    }

    @Override
    public void onApply() {
        super.onApply();
        IActiveCharacter character = (IActiveCharacter) getConsumer();
        character.setCharacterProperty(DefaultProperties.weapon_damage_bonus, character.getCharacterProperty(DefaultProperties.weapon_damage_bonus) + getBonusDamage());
    }

    @Override
    public void onRemove() {
        super.onRemove();
        IActiveCharacter character = (IActiveCharacter) getConsumer();
        character.setCharacterProperty(DefaultProperties.weapon_damage_bonus, character.getCharacterProperty(DefaultProperties.weapon_damage_bonus) - getBonusDamage());
    }
}
