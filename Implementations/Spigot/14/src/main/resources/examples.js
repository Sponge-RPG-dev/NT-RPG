var SkillSpeed = new (Java.extend(ActiveSkill, {
    init: function () {
        var s = Java.super(SkillSpeed);
        s.setCatalogId("myprefix:speed");
        s.setLocalizableName(to_text("Speed"));
        s.setDescription(to_multiline_text("For a duration boosts player's speed"));
        var SkillSpeedSettings = new SkillSettings();
        SkillSpeedSettings.addNode(SkillNodes.COOLDOWN, 8000, -500);
        SkillSpeedSettings.addNode(SkillNodes.MANACOST, 350, -10);
        SkillSpeedSettings.addNode(SkillNodes.DURATION, 5000, 1000);
        SkillSpeedSettings.addNode(SkillNodes.AMOUNT, 0.05, 0.06);
        s.setSettings(SkillSpeedSettings);
    },
    cast: function (character, extendedSkillInfo) {
        var duration = getLevelNode(extendedSkillInfo, SkillNodes.DURATION);
        var amount = getLevelNode(extendedSkillInfo, SkillNodes.AMOUNT);
        var speedEffect = new SpeedBoost(character, duration, amount);
        GlobalScope.effectService.addEffect(speedEffect, character, SkillSpeed);
        return SkillResult.OK;
    }
}));

var SuperJump = new (Java.extend(ActiveSkill, {
    init: function () {
        var s = Java.super(SuperJump);
        s.setCatalogId("myprefix:superjump");
        s.setLocalizableName(to_text("SuperJump"));
        s.setDescription(to_multiline_text("Launches player into air"));
        var SkillSpeedSettings = new SkillSettings();
        SkillSpeedSettings.addNode(SkillNodes.COOLDOWN, 8000, -500);
        SkillSpeedSettings.addNode(SkillNodes.MANACOST, 350, -10);
        SkillSpeedSettings.addNode("velocity", 30, 10);
        s.setSettings(SkillSpeedSettings);
    },
    cast: function (character, extendedSkillInfo) {
        var i = getLevelNode(extendedSkillInfo, "velocity");
        var newVector = new Vector3d(0, i, 0);
        character.getPlayer().offer(Keys.VELOCITY, newVector);
        character.sendMessage("You've used skill SuperJump");
        return SkillResult.OK;
    }
}));
var Heal = new (Java.extend(ActiveSkill, {
    init: function () {
        var s = Java.super(Heal);
        s.setCatalogId("myprefix:heal");
        s.setLocalizableName(to_text("Heal"));
        s.setDescription(to_multiline_text("After a delay heals the caster"));
        var HealSettings = new SkillSettings();
        HealSettings.addNode(SkillNodes.COOLDOWN, 80000, -300);
        HealSettings.addNode("delay", 3500, -100);
        HealSettings.addNode(SkillNodes.MANACOST, 150, 20);
        HealSettings.addNode("healed-amount", 1, 1);
        HealSettings.addNode("default-regen-mult", 1, 0.2);
        s.setSettings(HealSettings);
    },
    cast: function (character, extendedSkillInfo) {
        var delay = getLevelNode(extendedSkillInfo, "delay");
        GlobalScope.game.getScheduler()
              .createTaskBuilder()
              .delay(delay > 0 ? delay : 0, TimeUnit.MILLISECONDS)
              ["execute(Runnable)"](function (t) {
                  var healedamount = getLevelNode(extendedSkillInfo, "healed-amount") * getLevelNode(extendedSkillInfo, "default-regen-mult");
                  healedamount = GlobalScope.entityService.healEntity(character, healedamount, Heal);
                  character.sendMessage("You have been healed for " + healedamount);
              }).submit(GlobalScope.plugin);
        return SkillResult.OK;
    }
}));

registerSkill(Heal);
registerSkill(SuperJump);
registerSkill(SkillSpeed);

var eventConsumer = new (Java.extend(Consumer, {
    accept: function (event) {
        log("Im Javascript event handler");
    }
}))

//Only "consumer" property in second parameter is mandatory
/*var DamageEntityEvent = Java.type('org.spongepowered.api.event.network.ClientConnectionEvent')
registerEventListener({type: DamageEntityEvent, consumer: eventConsumer, order:"BEFORE_POST", beforeModifications: false});
*/