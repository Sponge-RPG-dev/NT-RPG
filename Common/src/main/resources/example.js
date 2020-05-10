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

registerEventListener({
    type: "org.spongepowered.api.event.network.ClientConnectionEvent",
    consumer: function(event) {
        log("I'm javascript Event handler")
    },
    order:"BEFORE_POST",
    beforeModifications: false
});
