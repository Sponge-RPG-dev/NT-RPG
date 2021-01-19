package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.Decorator;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:groupheal")
public class GroupHeal extends ActiveSkill<ISpongeCharacter> {

    @Inject
    private EntityService entityService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.RADIUS, 10);
        settings.addNode(SkillNodes.HEALED_AMOUNT, 10);
        addSkillType(SkillType.HEALING);
        addSkillType(SkillType.AOE);
    }

    @Override
    public SkillResult cast(ISpongeCharacter character, PlayerSkillContext skillContext) {
        float amnt = skillContext.getFloatNodeValue(SkillNodes.HEALED_AMOUNT);
        if (character.hasParty()) {
            double rad = Math.pow(skillContext.getDoubleNodeValue(SkillNodes.RADIUS), 2);
            for (ISpongeCharacter a : character.getParty().getPlayers()) {
                if (a.getLocation().getPosition().distanceSquared(character.getLocation().getPosition()) <= rad) {
                    entityService.healEntity(a, amnt, this);
                    Decorator.healEffect(a.getLocation());
                }
            }
        } else {
            entityService.healEntity(character, amnt, this);
            Decorator.healEffect(character.getEntity().getLocation().add(0, 1, 0));
        }

        return SkillResult.OK;
    }
}
