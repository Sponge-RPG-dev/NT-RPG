package cz.neumimto.skills;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@ResourceLoader.Skill
public class SkillLightning extends ActiveSkill {

    @Inject
    EntityService entityService;

    public SkillLightning() {
        setName("Lightning");
        setLore(SkillLocalization.SKILL_LIGHTNING_LORE);
        setDescription(SkillLocalization.SKILL_LIGHTNING_DESC);
        setDamageType(NDamageType.LIGHTNING);
        SkillSettings skillSettings = new SkillSettings();
        skillSettings.addNode(SkillNodes.DAMAGE, 10, 20);
        skillSettings.addNode(SkillNodes.RANGE,10 ,10);
        super.settings = skillSettings;
        setDamageType(NDamageType.LIGHTNING);
    }

    @Override
    public SkillResult cast(IActiveCharacter iActiveCharacter, ExtendedSkillInfo extendedSkillInfo,SkillModifier skillModifier) {
        int range = (int) settings.getLevelNodeValue(SkillNodes.RANGE,extendedSkillInfo.getTotalLevel());
        Living l = Utils.getTargettedEntity(iActiveCharacter,range);
        if (l == null)
            return SkillResult.NO_TARGET;
        IEntity e = entityService.get(l);
        if (e != null) {
            float damage = settings.getLevelNodeValue(SkillNodes.DAMAGE,extendedSkillInfo.getTotalLevel());
            SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
            build.fromSkill(this);
            build.setCaster(iActiveCharacter);
            build.type(getDamageType());
            l.damage(damage,build.build());
            l.getLocation().getExtent().createEntity(EntityTypes.LIGHTNING, l.getLocation().getPosition());

        }
        return SkillResult.OK;
    }


}
