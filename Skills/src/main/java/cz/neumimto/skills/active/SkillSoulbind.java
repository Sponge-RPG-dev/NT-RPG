package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.SoulBindEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.UUID;

/**
 * Created by NeumimTo on 5.2.2016.
 */
@ResourceLoader.Skill("ntrpg:soulbind")
@ResourceLoader.ListenerClass
public class SkillSoulbind extends ActiveSkill {

	public static final String name = "Soulbind";

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 1000f, 10f);
		settings.addNode(SkillNodes.COOLDOWN, 1000f, 10f);
		settings.addNode(SkillNodes.RANGE, 10f, 1f);
	}

	@Override
	public void cast(IActiveCharacter iActiveCharacter, PlayerSkillContext playerSkillContext, SkillContext skillContext) {
		float range = skillContext.getFloatNodeValue(SkillNodes.RANGE);
		Living targettedEntity = Utils.getTargettedEntity(iActiveCharacter, (int) range);
		if (targettedEntity != null && targettedEntity == EntityTypes.PLAYER) {
			IActiveCharacter character = characterService.getCharacter(targettedEntity.getUniqueId());
			if (iActiveCharacter.getParty().getPlayers().contains(character)) {
				SoulBindEffect effect = new SoulBindEffect(iActiveCharacter, character);
				effect.setDuration(skillContext.getLongNodeValue(SkillNodes.DURATION));
				effectService.addEffect(effect, iActiveCharacter, this);
				effectService.addEffect(effect, character, this);
			}
		}
		skillContext.next(iActiveCharacter, playerSkillContext, skillContext.result(SkillResult.OK));
	}

	@Listener(order = Order.LAST)
	public void onEntityDamage(DamageEntityEvent event) {
		if (event.isCancelled() || event.getFinalDamage() == 0) {
			return;
		}
		if (event.getTargetEntity().getType() == EntityTypes.PLAYER) {
			UUID id = event.getTargetEntity().getUniqueId();
			IActiveCharacter character = characterService.getCharacter(id);
			IEffectContainer container = character.getEffect(name);
			if (container == null) {
				return;
			}
			if (!event.getCause().first(SoulBindEffect.class).isPresent()) {
				event.setBaseDamage(event.getBaseDamage() * .5);
				SoulBindEffect effect = (SoulBindEffect) container;
				SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();

				if (effect.getConsumer() == character) {
					effect.getTarget().getEntity().damage(event.getBaseDamage(), builder.build());
				} else {
					effect.getConsumer().getEntity().damage(event.getBaseDamage(), builder.build());
				}
			}
		}
	}
}
