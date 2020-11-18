package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.F;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.slikey.effectlib.Effect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsBinding(JsBinding.Type.CONTAINER)
public class SpigotScriptFunctions {

    private static SpigotDamageService damageService;

    static {
        damageService = (SpigotDamageService) Rpg.get().getDamageService();
    }

    @SkillComponent(
            value = "Damaging an Entity with specific damage type",
            usage = "damage(source, target, damage, type)",
            params = {
                    @SkillComponent.Param("source - Entity of damage origin"),
                    @SkillComponent.Param("target - Entity to be damaged"),
                    @SkillComponent.Param("damage - damage value"),
                    @SkillComponent.Param("type - damage type"),
                    @SkillComponent.Param("@returns - true if the damage was dealt"),
            }
    )
    public static F.QuadFunction<ISpigotCharacter, IEntity<LivingEntity>, Number, EntityDamageEvent.DamageCause, Boolean> DAMAGE = (caster, target, damage, DamageCause) -> {
        if (damageService.canDamage(caster, target.getEntity())) {
            damageService.damage(caster.getEntity(), target.getEntity(), DamageCause, damage.doubleValue(), false);
            return true;
        }
        return false;
    };

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
    public static F.PentaFunction<IEntity<LivingEntity>, IEntity<LivingEntity>, Number, String, PlayerSkillContext, Boolean> DAMAGE_WITH_TYPE = (caster, target, damage, damageType, context) -> {
        //todo that spigot pr
        target.getEntity().damage(damage.doubleValue(), caster.getEntity());
        return true;
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
    public static BiFunction<String, PlayerSkillContext, Float> PARAM = (node, playerSkillContext) ->
            playerSkillContext.getFloatNodeValue(node);

    @SkillComponent(
            value = "Applies an effect to a specifc entity, each effect has different constructor parameters",
            usage = "apply_effect(effect, context, source)",
            params = {
                    @SkillComponent.Param("effect - The effect to be applied"),
                    @SkillComponent.Param("context - skill context"),
                    @SkillComponent.Param("source - source/caster entity (may be null)")
            }
    )
    public static TriConsumer<IEffect, PlayerSkillContext, IEntity> APPLY_EFFECT = (effect, context, source) -> {
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
        location.getWorld().strikeLightningEffect(location);
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
    public static TriConsumer<IEntity, Number, PlayerSkillContext> HEAL = (iEntity, aNumber, context) ->
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

    @SkillComponent(
            value = "Sets entity velocity",
            usage = "set_velocity(entity, x, y, z)",
            params = {
                    @SkillComponent.Param("entity - target minecraft entity"),
                    @SkillComponent.Param("x - Velocity vector - x"),
                    @SkillComponent.Param("y - Velocity vector - y"),
                    @SkillComponent.Param("z - Velocity vector - z")
            }
    )
    public static F.QuadConsumer<Entity, Double, Double, Double> SET_VELOCITY = (entity, x, y, z) -> {
        entity.setVelocity(new Vector(x,y,z));
    };

}
