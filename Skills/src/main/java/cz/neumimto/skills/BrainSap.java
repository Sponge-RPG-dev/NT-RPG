package cz.neumimto.skills;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

/**
 * Created by NeumimTo on 5.2.2016.
 */
@ResourceLoader.Skill
@ResourceLoader.ListenerClass
public class BrainSap extends ActiveSkill {

    @Inject
    private EntityService entityService;

    public BrainSap() {
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNodes.COOLDOWN,1000f,10f);
        settings.addNode(SkillNodes.RANGE,10f,1f);
        settings.addNode(SkillNodes.DAMAGE,10f,10f);
        setLore(SkillLocalization.SKILL_BRAINSAP_LORE);
        super.settings = settings;
        setName("BrainSap");
        setDescription(SkillLocalization.SKILL_BRAINSAP_DESC);
    }

    @Override
    public SkillResult cast(IActiveCharacter iActiveCharacter, ExtendedSkillInfo extendedSkillInfo, SkillModifier skillModifier) {
        float range = extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.RANGE,extendedSkillInfo.getLevel());
        Living targettedEntity = Utils.getTargettedEntity(iActiveCharacter, (int) range);
        if (targettedEntity != null) {
            SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
            builder.setSkill(this);
            builder.setCaster(iActiveCharacter);
            SkillDamageSource s = builder.build();
            IEntity entity = entityService.get(targettedEntity);
            float damage = extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.DAMAGE,extendedSkillInfo.getLevel());
            entity.getEntity().damage(damage,s);
            return SkillResult.OK;
        }
        return SkillResult.CANCELLED;
    }

    @Listener(order = Order.LAST)
    public void onDamage(DamageEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getCause().first(SkillDamageSource.class).isPresent()) {
            SkillDamageSource source = event.getCause().first(SkillDamageSource.class).get();
            if (source.getSkill() == this) {
                IActiveCharacter caster = source.getCaster();
                characterService.healCharacter(caster, (float) event.getFinalDamage());
            }
        }
    }

}
