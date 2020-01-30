package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.F;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.entity.LivingEntity;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.info;

@JsBinding(JsBinding.Type.CONTAINER)
public class SkillActions {

    @SkillComponent(
            value = "Damaging an Entity",
            usage = "damage(source, target, damage, context)",
            params = {
                    @SkillComponent.Param("source - Entity of damage origin"),
                    @SkillComponent.Param("target - Entity to be damaged"),
                    @SkillComponent.Param("damage - damage value"),
                    @SkillComponent.Param("context - skill context"),
                    @SkillComponent.Param("@returns - true if the damage was dealt"),
            }
    )
    public static F.QuadFunction<IEntity<LivingEntity>, IEntity<LivingEntity>, Number, SkillScriptContext, Boolean> DAMAGE = (caster, target, damage, context) -> {
        IEntity iEntity;
        ISpigotEntity<LivingEntity> l;
        l.setSkillOrEffectDamageCause()
        LivingEntity entity = target.getEntity();
        LivingEntity damager = caster.getEntity();

        return entity.damage(damage.doubleValue(), damager);
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
    public static F.PentaFunction<IEntity<LivingEntity>, IEntity<LivingEntity>, Number, String, SkillScriptContext, Boolean> DAMAGE_WITH_TYPE = (caster, target, damage, damageType, context) -> {
        Optional<DamageType> type = Sponge.getRegistry().getType(DamageType.class, damageType);
        if (!type.isPresent()) {
            throw new RuntimeException(
                    String.format("Unknown damage type %s, Use one of [%s]",
                            damageType,
                            Sponge.getRegistry().getAllOf(DamageType.class).stream().map(DamageType::getId).collect(Collectors.joining(", "))
                    )
            );
        }
        SkillDamageSource s = new SkillDamageSourceBuilder()
                .fromSkill(context.getSkill())
                .type(type.get())
                .setSource(caster)
                .build();
        return target.getEntity().damage(damage.doubleValue(), s);
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
    public static TriConsumer<Location, Number, String> BROADCAST_NEARBY = (location, integer, string) -> {
        Text text = TextHelper.parse(string);
        double pow = Math.pow(integer.intValue(), 2);

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

    @SkillComponent(
            value = "Adds potion effect to the entity",
            usage = "add_potion_effect(target, builder)",
            params = {
                    @SkillComponent.Param("target - An entitz"),
                    @SkillComponent.Param("builder - potion effect builder"),
            }
    )
    public static BiConsumer<ISpongeEntity, PotionEffect.Builder> ADD_POTION_EFFECT = (iEffectConsumer, builder) -> {
        PotionEffect build = builder.build();
        iEffectConsumer.addPotionEffect(build);
    };

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
            value = "Returns an instance of PotionEffectBuilder",
            usage = "var builder = potion_effect_builder(\"potioneffectid\")",
            params = {}
    )
    public static Function<String, PotionEffect.Builder> POTION_EFFECT_BUILDER = s -> {
        PotionEffectType type = Sponge.getRegistry().getType(PotionEffectType.class, s).orElseGet(null);
        if (type == null) {
            info("Registered Potion Ids:");
            for (PotionEffectType potionEffectType : Sponge.getRegistry().getAllOf(PotionEffectType.class)) {
                info(" - " + potionEffectType.getId());
            }
            throw new RuntimeException("Unknown Potion Effect Id : " + s);
        }
        return PotionEffect.builder().potionType(type);
    };

    @SkillComponent(
            value = "Converts a string to Text object.",
            usage = "var text = to_text(\"&aColored Text\")",
            params = {}
    )
    public static Function<String, Text> TO_TEXT = TextHelper::parse;

    @SkillComponent(
            value = "Converts a string to multiline (list) of Text objects.",
            usage = "var textList = to_multiline_text(\"&aFirst Line:nSecondLine\")",
            params = {}
    )
    public static Function<String, List<Text>> TO_MULTILINE_TEXT = TextHelper::splitStringByDelimiter;

}
