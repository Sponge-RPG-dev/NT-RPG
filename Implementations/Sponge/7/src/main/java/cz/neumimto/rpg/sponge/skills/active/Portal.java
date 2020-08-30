package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.effects.positive.PortalEffect;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 22.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:portal")
public class Portal extends ActiveSkill {

    @Inject
    private EffectService effectService;


    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.COOLDOWN, 100000, -500);
        settings.addNode("chance-to-fail", 80, -50);
        settings.addNode("manacost-per-tick", 20, 5);
        settings.addNode("portal-duration", 20, 20);
        settings.addNode("manacost-per-teleported-entity", 5, 7);
        addSkillType(SkillType.UTILITY);
        addSkillType(SkillType.TELEPORT);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext skillContext) {
        if (character.hasEffect(PortalEffect.name)) {
            effectService.removeEffect(PortalEffect.name, character, this);
            return SkillResult.CANCELLED;
        }
        long duration = skillContext.getLongNodeValue(SkillNodes.MANACOST);
        double manaPerTick = skillContext.getDoubleNodeValue("manacost-per-tick");
        double manaPerEntity = skillContext.getDoubleNodeValue("manacost-per-teleported-entity");
        double chanceToFail = skillContext.getDoubleNodeValue("chance-to-fail");
        PortalEffect portalEffect = new PortalEffect(character, duration, null,
                manaPerTick, manaPerEntity, 1750, chanceToFail, false);
        effectService.addEffect(portalEffect, this);
        return SkillResult.OK;
    }


}
