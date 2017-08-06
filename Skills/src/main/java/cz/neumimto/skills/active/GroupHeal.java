package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@ResourceLoader.Skill
public class GroupHeal extends ActiveSkill {

    @Inject
    private EntityService entityService;

    public GroupHeal() {
        setName(SkillLocalization.SKILL_GROUPHEAL_NAME);
        setDescription(SkillLocalization.SKILL_GROUPHEAL_DESC);
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNodes.RADIUS, 10, 10);
        settings.addNode(SkillNodes.HEALED_AMOUNT, 10, 10);
        setSettings(settings);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
        double rad = Math.pow(getDoubleNodeValue(info, SkillNodes.RADIUS),2);
        float amnt = getFloatNodeValue(info, SkillNodes.HEALED_AMOUNT);
        for (IActiveCharacter a : character.getParty().getPlayers()) {
            if (a.getLocation().getPosition().distanceSquared(character.getLocation().getPosition()) <= rad) {
                entityService.healEntity(a, amnt, this);
                Decorator.healEffect(a.getLocation());
            }
        }
        return SkillResult.OK;
    }
}
