package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.F;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@JsBinding(JsBinding.Type.CONTAINER)
public class SkillActions {

    @SkillComponent(
            value = "Damaging an Entity with specific damage type",
            usage = "damage(source, target, damage, type, context)",
            params = {
                    @SkillComponent.Param("source - Entity of damage origin"),
                    @SkillComponent.Param("target - Entity to be damaged"),
                    @SkillComponent.Param("damage - damage value"),
                    @SkillComponent.Param("type - damage type"),
                    @SkillComponent.Param("context - skill context"),
                    @SkillComponent.Param("@returns - true if the damage was dealt"),
            }
    )
    public static F.PentaFunction<IEntity<LivingEntity>, IEntity<LivingEntity>, Number, String, SkillScriptContext, Void> DAMAGE_WITH_TYPE = (caster, target, damage, DamageCause, context) -> {
        EntityDamageEvent.DamageCause type = EntityDamageEvent.DamageCause.valueOf(DamageCause.toUpperCase());

        target.getEntity().damage(damage.doubleValue(), caster.getEntity());
        return null;
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
            usage = "apply_effect(effect, context, source)",
            params = {
                    @SkillComponent.Param("effect - The effect to be applied"),
                    @SkillComponent.Param("context - skill context"),
                    @SkillComponent.Param("source - source/caster entity (may be null)")
            }
    )
    public static TriConsumer<IEffect, SkillScriptContext, IEntity> APPLY_EFFECT = (effect, context, source) -> {
        Rpg.get().getEffectService().addEffect(effect, context.getSkill(), source);
    };

    @SkillComponent(
            value = "Sends a text message to all players on the server",
            usage = "broadcast_all(\"&4Some colored text\")",
            params = {
                    @SkillComponent.Param("")
            }
    )
    public static Consumer<String> BROADCAST_ALL = (string) -> {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(string));
    };

    @SkillComponent(
            value = "Sends a text message to all players in a radius around target",
            usage = "broadcast_nearby(location,radius,\"&4Some colored text\")",
            params = {
                    @SkillComponent.Param("location - Origin"),
                    @SkillComponent.Param("radius - An integer"),
            }
    )
    public static TriConsumer<Location, Number, String> BROADCAST_NEARBY = (location, integer, string) -> {

        double pow = Math.pow(integer.intValue(), 2);

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            if (onlinePlayer.getLocation().getWorld().equals(location.getWorld())
                    && onlinePlayer.getLocation().distanceSquared(location) < pow) {
                onlinePlayer.sendMessage(string);
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
        Entity q = location.getWorld().spawnEntity(location, EntityType.LIGHTNING);
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
    public static TriConsumer<IEntity, Number, SkillScriptContext> HEAL = (iEntity, aNumber, context) ->
            Rpg.get().getEntityService().healEntity(iEntity, aNumber.floatValue(), context.getSkill());


    @SkillComponent(
            value = "Returns entity location",
            usage = "get_location(player_or_entity)",
            params = {
                    @SkillComponent.Param("entity - An entitz"),
                    @SkillComponent.Param("@return - location object"),
            }
    )
    public static Function<IEntity<LivingEntity>, Location> GET_LOCATION = iEntity -> iEntity.getEntity().getLocation();

    /*
    @SkillComponent(
            value = "Adds potion effect to the entity",
            usage = "add_potion_effect(target, builder)",
            params = {
                    @SkillComponent.Param("target - An entitz"),
                    @SkillComponent.Param("builder - potion effect builder"),
            }
    )
    public static BiConsumer<ISpigotEntity, PotionEffect> ADD_POTION_EFFECT = (iEffectConsumer, oe) -> {

    };
*/
    @SkillComponent(
            value = "Converts a time to ingame interval represented in server ticks, conversion is not taking into account server lag",
            usage = "to_server_ticks(100, timeunit)",
            params = {
                    @SkillComponent.Param("number - time to be converted"),
                    @SkillComponent.Param("timeunit - an enum value of TimeUnit"),
            }
    )
    public static BiFunction<Long, TimeUnit, Integer> TO_SERVER_TICKS = (o, o2) -> {
        long convert = o2.convert(o, TimeUnit.SECONDS);
        return (int) (convert / 3);
    };

}
