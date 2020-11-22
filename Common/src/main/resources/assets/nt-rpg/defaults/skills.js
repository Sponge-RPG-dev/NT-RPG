registerSkillHandler('mynamespace:speed_boost',{
    onCast: function (character, context) {
        var duration = context.getLevelNode(extendedSkillInfo, SkillNodes.DURATION);
        var amount = getLevelNode(extendedSkillInfo, SkillNodes.AMOUNT);
        apply_effect(new SpeedBoost(character, duration, amount), context.getSkill());
        return SkillResult.OK;
    }
})

registerSkillHandler('mynamespace:jump_vertical',{
    onCast: function(character, context) {
       var i = getLevelNode(context, "velocity");
       set_velocity(character, 0, i, 0);
       return SkillResult.OK;
    }
})

registerSkillHandler('ntrpg:aoe_lightning_damage', {
    onCast: function(character, context) {
        var totalDamage = param("damage", context);
        var totalRange = param("radius", context);
        for_each_nearby_enemy(character, totalRange, function(entity) {
            var location = get_location(entity);
            if (damage(character, entity, totalDamage, DamageCause.MAGIC, context)) {
                spawn_lightning(location);
            }
        });
        return SkillResult.OK;
    }
});

registerSkillHandler('ntrpg:periodic_regeneration', {
    onCast: function(character, context) {
        var variant = new VitalizeEffectModel();
        variant.duration = param("duration", context);
        variant.period = param("tick-rate", context);
        variant.manaPerTick = param("mana-per-tick", context);
        variant.healthPerTick = param("health-per-tick", context);

        // Applies effect to the skill caster
        // depending on the parent node in configuration above character variable might or might not be accessible
        // for example if the parent node is set to value targetted within the skill scope you will have to reference _target instead. _target may return the caster, if the skill has no damage type, and caster has not aiming at any entity
        apply_effect(new VitalizeEffect(character, variant), context.getSkill());
        return SkillResult.OK;
    }
});

registerEventListener({
    type: "org.spongepowered.api.event.network.ClientConnectionEvent",
    consumer: function (event) {
        log("I'm javascript Event handler")
    },
    order:"BEFORE_POST",
    beforeModifications: false
});

if (Rpg.getPlatform().equals("Spigot")) {

    var AbstractBeam = Java.type("cz.neumimto.rpg.spigot.skills.utils.AbstractBeam");
    var Decorator = Java.type("cz.neumimto.rpg.spigot.skills.utils.Decorator");
    var Material = Java.type("org.bukkit.Material");
    var Particle = Java.type("org.bukkit.Particle");
    var DamageCause = Java.type("org.bukkit.event.entity.EntityDamageEvent.DamageCause")

    //https://www.spigotmc.org/threads/comprehensive-particle-spawning-guide-1-13.343001/


    var IceShotBeam = Java.extend(AbstractBeam, {
        onEntityHit: function(caster, hitEntity, data, tick) {
            if (damage(caster, hitEntity, data.damage, DamageCause.MAGIC)) {
                var model = new SlowModel();
                model.slowLevel = 2
                model.decreasedJumpHeight = false;
                apply_effect(new SlowEffect(hitEntity, data.slowDuration, model), data.ctx, data.caster);
            }
            return true;
        },
        onTick: function(location, data, tick) {
            Decorator.point2(location, Particle.BLOCK_CRACK, 3, Material.ICE)
            Decorator.point0(location, Particle.CLOUD, 2)
        },
        onBlockHit: function(block, data, tick) {
            return true;
        }
    });

registerSkillHandler('ntrpg:iceshot', {
    onCast: function(character, context) {
        var beam = new IceShotBeam();
        beam.setData({
            damage: param("damage", context),
            ctx: context,
            caster: character,
            slowDuration: param("slow-duration", context)
        });
        beam.init(character, param("max-distance", context), 2);
        // delay, tick period period
        beam.start(0, 1);
        return SkillResult.OK;
    }
});
}

if (Rpg.getPlatform().equals("Sponge")) {

}