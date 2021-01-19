package cz.neumimto.rpg.sponge.skills.passive;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.sponge.effects.EnderPearlEffect;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 7.8.17.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:enderpearl")
public class EnderPearl extends PassiveSkill {


    public EnderPearl() {
        super(EnderPearlEffect.name);
        settings.addNode(SkillNodes.COOLDOWN, 7500);
        addSkillType(SkillType.TELEPORT);
        addSkillType(SkillType.MOVEMENT);
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
        long cooldown = info.getLongNodeValue(SkillNodes.COOLDOWN);
        EnderPearlEffect effect = new EnderPearlEffect(character, -1L, cooldown);
        effectService.addEffect(effect, this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(IActiveCharacter, level, context);
        PlayerSkillContext skill = IActiveCharacter.getSkill(getId());
        long cooldown = skill.getLongNodeValue(SkillNodes.COOLDOWN);
        IEffectContainer<Long, EnderPearlEffect> container = IActiveCharacter.getEffect(EnderPearlEffect.name);
        container.updateValue(cooldown, this);
    }
}