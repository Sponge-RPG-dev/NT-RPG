package cz.neumimto.rpg.sponge.skills.active;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.effects.positive.SoulBindEffect;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

/**
 * Created by NeumimTo on 5.2.2016.
 */
@Singleton
@ResourceLoader.ListenerClass
@ResourceLoader.Skill("ntrpg:soulbind")
public class SkillSoulbind extends ActiveSkill<ISpongeCharacter> {

    public static final String name = "Soulbind";

    @Inject
    private EffectService effectService;

    @Inject
    private SpongeCharacterService characterServise;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.DURATION, 1000f);
        settings.addNode(SkillNodes.COOLDOWN, 1000f);
        settings.addNode(SkillNodes.RANGE, 10f);
    }

    @Override
    public SkillResult cast(ISpongeCharacter iActiveCharacter, PlayerSkillContext skillContext) {
        float range = skillContext.getFloatNodeValue(SkillNodes.RANGE);
        Living targettedEntity = Utils.getTargetedEntity(iActiveCharacter, (int) range);
        if (targettedEntity != null && targettedEntity.getType() == EntityTypes.PLAYER) {
            ISpongeCharacter character = characterServise.getCharacter(targettedEntity.getUniqueId());
            if (iActiveCharacter.getParty().getPlayers().contains(character)) {
                SoulBindEffect effect = new SoulBindEffect(iActiveCharacter, character);
                effect.setDuration(skillContext.getLongNodeValue(SkillNodes.DURATION));
                effectService.addEffect(effect, this);
                effectService.addEffect(effect, this);
            }
        }
        return SkillResult.OK;
    }

    @Listener(order = Order.LAST)
    public void onEntityDamage(DamageEntityEvent event) {
        if (event.getFinalDamage() == 0) {
            return;
        }
        if (event.getTargetEntity().getType() == EntityTypes.PLAYER) {
            UUID id = event.getTargetEntity().getUniqueId();
            ISpongeCharacter character = characterServise.getCharacter(id);
            IEffectContainer container = character.getEffect(name);
            if (container == null) {
                return;
            }
            if (!event.getCause().first(SoulBindEffect.class).isPresent()) {
                event.setBaseDamage(event.getBaseDamage() * .5);
                SoulBindEffect effect = (SoulBindEffect) container;
                SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();

                if (effect.getConsumer() == character) {
                    ((ISpongeEntity) effect.getTarget()).getEntity().damage(event.getBaseDamage(), builder.build());
                } else {
                    ((ISpongeEntity) effect.getTarget()).getEntity().damage(event.getBaseDamage(), builder.build());
                }
            }
        }
    }
}
