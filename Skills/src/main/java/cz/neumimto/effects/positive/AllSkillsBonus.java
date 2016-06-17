package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;

@ClassGenerator.Generate(id = "name")
public class AllSkillsBonus extends EffectBase{

    public static final String name = "All skills";
    IActiveCharacter character1;
    private int bonus;
    public AllSkillsBonus(IActiveCharacter character, long duration, float level) {
        character1 = character;
        setDuration(duration);
        setStackable(false);
        this.bonus = (int) level;
    }

    @Override
    public void onApply() {
        character1.setCharacterProperty(DefaultProperties.all_skills_bonus,character1.getCharacterProperty(DefaultProperties.all_skills_bonus)+bonus);
    }

    @Override
    public void onRemove() {
        character1.setCharacterProperty(DefaultProperties.all_skills_bonus,character1.getCharacterProperty(DefaultProperties.all_skills_bonus)-bonus);
    }

    @Override
    public IEffectConsumer getConsumer() {
        return character1;
    }
}
