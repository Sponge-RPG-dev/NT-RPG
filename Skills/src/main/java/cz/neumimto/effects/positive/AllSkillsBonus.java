package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.common.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.utils.Utils;

@ClassGenerator.Generate(id = "name")
public class AllSkillsBonus extends EffectBase<Integer> {

    public static final String name = "All skills";

    public AllSkillsBonus(IEffectConsumer character, long duration, int value) {
        super(name, character);
        setDuration(duration);
        setStackable(true, new IntegerEffectStackingStrategy());
        setValue(value);
    }

    public AllSkillsBonus(IEffectConsumer character, long duration, String value) {
        this(character, duration, Integer.parseInt(Utils.extractNumber(value)));
    }

    @Override
    public void onApply() {
        getConsumer().setProperty(DefaultProperties.all_skills_bonus,getConsumer().getProperty(DefaultProperties.all_skills_bonus)+getValue());
    }

    @Override
    public void onRemove() {
        getConsumer().setProperty(DefaultProperties.all_skills_bonus,getConsumer().getProperty(DefaultProperties.all_skills_bonus)-getValue());
    }

}
