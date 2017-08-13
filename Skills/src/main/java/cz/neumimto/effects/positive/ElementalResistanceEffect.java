package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.PropertyContainer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 29.3.17.
 */
@ClassGenerator.Generate(id = "name")
public class ElementalResistanceEffect extends EffectBase<Float> {

    public static final String name = "Elemental Resistance";

    public ElementalResistanceEffect(IEffectConsumer consumer, float percentage, long duration) {
        super(name, consumer);
        setStackable(true, new FloatEffectStackingStrategy());
        setValue(percentage);
        setDuration(duration);
    }

    public ElementalResistanceEffect(IEffectConsumer character, long duration, String level) {
        this(character, Float.parseFloat(level), duration);
    }

    @Override
    public void onApply() {
        super.onApply();
        apply(1, getConsumer());
    }

    private void apply(int i, PropertyContainer propertyContainer) {
        float characterProperty = propertyContainer.getProperty(DefaultProperties.fire_damage_protection_mult);
        propertyContainer.setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + getValue()* i);

        characterProperty = propertyContainer.getProperty(DefaultProperties.ice_damage_protection_mult);
        propertyContainer.setProperty(DefaultProperties.ice_damage_protection_mult, characterProperty + getValue() * i);

        characterProperty = propertyContainer.getProperty(DefaultProperties.lightning_damage_protection_mult);
        propertyContainer.setProperty(DefaultProperties.lightning_damage_protection_mult, characterProperty + getValue() * i);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        apply(-1, getConsumer());
    }
}
