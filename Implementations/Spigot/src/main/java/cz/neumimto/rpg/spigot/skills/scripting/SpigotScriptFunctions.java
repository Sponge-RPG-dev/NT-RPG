package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.F;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.utils.TriConsumer;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@JsBinding(JsBinding.Type.CONTAINER)
public class SpigotScriptFunctions {

    private static SpigotDamageService damageService;

    static {
        damageService = (SpigotDamageService) Rpg.get().getDamageService();
    }

    public static F.QuadFunction<ISpigotCharacter, IEntity<LivingEntity>, Number, EntityDamageEvent.DamageCause, Boolean> DAMAGE = (caster, target, damage, DamageCause) -> {
        if (damageService.canDamage(caster, target.getEntity())) {
            damageService.damage(caster.getEntity(), target.getEntity(), DamageCause, damage.doubleValue(), false);
            return true;
        }
        return false;
    };

    public static F.PentaFunction<IEntity<LivingEntity>, IEntity<LivingEntity>, Number, String, PlayerSkillContext, Boolean> DAMAGE_WITH_TYPE = (caster, target, damage, damageType, context) -> {
        //todo that spigot pr
        target.getEntity().damage(damage.doubleValue(), caster.getEntity());
        return true;
    };

    public static BiFunction<String, PlayerSkillContext, Double> PARAM = (node, playerSkillContext) ->
            playerSkillContext.getFloatNodeValue(node);

    public static TriConsumer<IEffect, PlayerSkillContext, IEntity> APPLY_EFFECT = (effect, context, source) -> {
        Rpg.get().getEffectService().addEffect(effect, context.getSkill(), source);
    };

    public static Consumer<String> BROADCAST_ALL = (string) -> {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(string));
    };

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

    public static Consumer<Location> SPAWN_LIGHTNING = (location) -> {
        location.getWorld().strikeLightningEffect(location);
    };

    public static TriConsumer<IEntity, Number, PlayerSkillContext> HEAL = (iEntity, aNumber, context) ->
            Rpg.get().getEntityService().healEntity(iEntity, aNumber.floatValue(), context.getSkill());


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
    public static BiFunction<Long, TimeUnit, Integer> TO_SERVER_TICKS = (o, o2) -> {
        long convert = o2.convert(o, TimeUnit.SECONDS);
        return (int) (convert / 3);
    };

    public static F.QuadConsumer<Entity, Double, Double, Double> SET_VELOCITY = (entity, x, y, z) -> {
        entity.setVelocity(new Vector(x, y, z));
    };

}
