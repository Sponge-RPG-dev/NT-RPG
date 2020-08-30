package cz.neumimto.rpg.sponge.skills.passive;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.sponge.effects.positive.DodgeEffect;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:dodge")
public class Dodge extends PassiveSkill {

    public Dodge() {
        super(DodgeEffect.name);
        settings.addNode(SkillNodes.CHANCE, 10, 20);
        addSkillType(SkillType.PHYSICAL);
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
        int totalLevel = info.getTotalLevel();
        float chance = info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.CHANCE, totalLevel);
        DodgeEffect dodgeEffect = new DodgeEffect(character, -1, chance);
        effectService.addEffect(dodgeEffect, this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter character, int level, PlayerSkillContext context) {
        PlayerSkillContext info = character.getSkill(getId());
        int totalLevel = info.getTotalLevel();
        float chance = info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.CHANCE, totalLevel);
        IEffectContainer<Float, DodgeEffect> effect = character.getEffect(DodgeEffect.name);
        effect.updateValue(chance, this);
    }
}
