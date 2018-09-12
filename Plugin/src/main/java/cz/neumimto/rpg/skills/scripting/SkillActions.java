package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.pipeline.SkillComponent;
import cz.neumimto.rpg.skills.utils.F.QuadConsumer;
import cz.neumimto.rpg.utils.TriConsumer;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@JsBinding(JsBinding.Type.CONTAINER)
public class SkillActions {

	@SkillComponent(
			value = "Damaging an Entity",
			usage = "damage(caster, target, damage, context)",
			params = {
					@SkillComponent.Param("caster - Entity of damage origin"),
					@SkillComponent.Param("target - Entity to be damaged"),
					@SkillComponent.Param("damage - damage value"),
					@SkillComponent.Param("context - skill context"),
			}
	)
	public static QuadConsumer<IEntity, IEntity, Double, SkillScriptContext> DAMAGE = (caster, target, damage, context) -> {
		if (Utils.canDamage(caster, target.getEntity())) {
			SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
			builder.fromSkill(context.getSkill());
			builder.setCaster(caster);
			target.getEntity().damage(damage, builder.build());
		}
	};

	@SkillComponent(
			value = "Returns a float value of a skill node. \n"
					+ " The returned value is a sum of initial value X and skill level * X_levelbonus nodes  ",
			usage = "param(node, context)",
			params = {
					@SkillComponent.Param("node - setting string key"),
					@SkillComponent.Param("context - skill context"),
					@SkillComponent.Param("@returns - float value"),
			}
	)
	public static BiFunction<String, SkillScriptContext, Float> PARAM = (node, skillScriptContext) ->
			skillScriptContext.getSkillInfo().getSkillData()
					.getSkillSettings()
					.getLevelNodeValue(node, skillScriptContext.getSkillInfo().getTotalLevel());

	@SkillComponent(
			value = "Applies an effect to a specifc entity, each effect has different constructor parameters",
			usage = "apply_effect(effect, target, context)",
			params = {
					@SkillComponent.Param("effect - The effect to be applied"),
					@SkillComponent.Param("target - Entity to consume the effect"),
					@SkillComponent.Param("context - skill context"),
			}
	)
	public static TriConsumer<IEffect, IEffectConsumer, SkillScriptContext> APPLY_EFFECT = (iEffect1, iEffectConsumer, skillScriptContext) -> {
		NtRpgPlugin.GlobalScope.effectService.addEffect(iEffect1, iEffectConsumer, skillScriptContext.getSkill());
	};

	@SkillComponent(
			value = "Sends a text message to all players on the server",
			usage = "broadcast_all(\"&4Some colored text\")",
			params = {
					@SkillComponent.Param("")
			}
	)
	public static Consumer<String> BROADCAST_ALL = (string) -> {
		Text text = TextHelper.parse(string);
		Sponge.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(text));
	};

	@SkillComponent(
			value = "Sends a text message to all players in a radius around target",
			usage = "broadcast_nearby(location,radius,\"&4Some colored text\")",
			params = {
					@SkillComponent.Param("location - Origin"),
					@SkillComponent.Param("radius - An integer"),
			}
	)
	public static TriConsumer<Location, Integer, String> BROADCAST_NEARBY = (location, integer, string) -> {
		Text text = TextHelper.parse(string);
		double pow = Math.pow(integer, 2);

		Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();
		for (Player onlinePlayer : onlinePlayers) {
			if (onlinePlayer.getLocation().getExtent().equals(location.getExtent())
					&& onlinePlayer.getLocation().getPosition().distanceSquared(location.getPosition()) < pow) {
				onlinePlayer.sendMessage(text);
			}
		}
	};

	@SkillComponent(
			value = "Spawns a lightning at a specific location",
			usage = "spawn_lightning(location)",
			params = {
					@SkillComponent.Param("location - Origin"),
					@SkillComponent.Param("radius - An integer"),
			}
	)
	public static Consumer<Location> SPAWN_LIGHTNING = (location) -> {
		Entity q = location.getExtent().createEntity(EntityTypes.LIGHTNING, location.getPosition());
		location.getExtent().spawnEntity(q);
	};

	@SkillComponent(
			value = "Heals an entity",
			usage = "heal(target, amount, context)",
			params = {
					@SkillComponent.Param("target - Entity to be healed"),
					@SkillComponent.Param("amount - Amount of HP to be healed, amount > 0"),
					@SkillComponent.Param("context - skill context"),
			}
	)
	public static TriConsumer<IEntity, Float, SkillScriptContext> HEAL = (iEntity, aFloat, context) ->
		NtRpgPlugin.GlobalScope.entityService.healEntity(iEntity, aFloat, context.getSkill());


	@SkillComponent(
			value = "Returns entity location",
			usage = "get_location(player_or_entity)",
			params = {
					@SkillComponent.Param("entity - An entitz"),
					@SkillComponent.Param("@return - location object"),
			}
	)
	public static Function<IEntity, Location> GET_LOCATION = iEntity -> iEntity.getEntity().getLocation();

}
