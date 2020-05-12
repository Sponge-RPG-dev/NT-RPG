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
        var totalRange = param("range", context);

        for_each_nearby_enemy(character, totalRange, function(entity) {
            var location = get_location(entity);
            if (damage(character, entity, totalDamage, context)) {
                spawn_lightning(location);
            }
        });
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
