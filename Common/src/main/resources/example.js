registerSkillHandler('mynamespace:speed_boost',{
    onCast: function (character, context) {
        var duration = context.getLevelNode(extendedSkillInfo, SkillNodes.DURATION);
        var amount = getLevelNode(extendedSkillInfo, SkillNodes.AMOUNT);
        var speedEffect = new SpeedBoost(character, duration, amount);
        Rpg.getEffectService().addEffect(speedEffect, character, SkillSpeed);
        return SkillResult.OK;
    }
})

registerSkillHandler('mynamespace:jump_vertical',{
    onCast: function(character, context) {
       var i = getLevelNode(extendedSkillInfo, "velocity");
       var newVector = new Vector3d(0, i, 0);
       character.getPlayer().offer(Keys.VELOCITY, newVector);
       return SkillResult.OK;
    }
})

registerSkillHandler('ntrpg:megabolt', {
    onCast: function(character, context) {
        var totalDamage = param("damage", _context);
        var totalRange = param("range", _context);

        for_each_nearby_enemy(_caster, totalRange, function(entity) {
            var location = get_location(entity);
            if (damage(_caster, entity, totalDamage, _context)) {
                spawn_lightning(location);
            }
        });
    }
});

registerSkillHandler('ntrpg:heal', {
    onCast: function(character, context) {
        var variant = new VitalizeEffectModel();
        variant.duration = param("duration", _context);
        variant.period = param("tick-rate", _context);
        variant.manaPerTick = param("mana-per-tick", _context);
        variant.healthPerTick = param("health-per-tick", _context);

        // Applies effect to the skill caster
        // depending on the parent node in configuration above _caster variable might or might not be accessible
        // for example if the parent node is set to value targetted within the skill scope you will have to reference _target instead. _target may return the caster, if the skill has no damage type, and caster has not aiming at any entity
        apply_effect(new VitalizeEffect(_caster, variant), context.getSkill());
        return SkillResult.OK;
    }
});

registerEventListener({
    type: "org.spongepowered.api.event.network.ClientConnectionEvent",
    consumer: function(event) {
        log("I'm javascript Event handler")
    },
    order:"BEFORE_POST",
    beforeModifications: false
});
