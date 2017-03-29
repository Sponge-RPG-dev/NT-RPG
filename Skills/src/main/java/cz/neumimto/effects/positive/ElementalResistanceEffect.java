package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.PropertyContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 29.3.17.
 */
@ClassGenerator.Generate(id = "name")
public class ElementalResistanceEffect extends EffectBase<ElementalResistanceEffect> {

    public static final String name = "Elemental Resistance";

    private float percentage;

    public ElementalResistanceEffect(String name, IEffectConsumer consumer, float percentage, long duration) {
        super(name, consumer);
        setStackable(true);
        setDuration(duration);
        this.percentage = percentage;
    }

    public ElementalResistanceEffect(IActiveCharacter character, long duration, String level) {
        this(name, character, Float.parseFloat(level), duration);
    }

    @Override
    public void onStack(ElementalResistanceEffect effect) {
        apply(-1, getConsumer());
        this.percentage += percentage + effect.percentage;
        apply(1, getConsumer());
    }

    @Override
    public void onApply() {
        super.onApply();
        apply(1, getConsumer());
    }

    private void apply(int i, PropertyContainer propertyContainer) {
        float characterProperty = propertyContainer.getProperty(DefaultProperties.fire_damage_protection_mult);
        propertyContainer.setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + percentage * i);

        characterProperty = propertyContainer.getProperty(DefaultProperties.ice_damage_protection_mult);
        propertyContainer.setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + percentage * i);

        characterProperty = propertyContainer.getProperty(DefaultProperties.lightning_damage_protection_mult);
        propertyContainer.setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + percentage * i);
    }



    @Override
    public void onRemove() {
        super.onRemove();
        apply(-1, getConsumer());
    }
}
