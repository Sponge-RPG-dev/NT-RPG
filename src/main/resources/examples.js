var SkillSpeed =new (Java.extend(ActiveSkill, {
    init: function() {
        var s = Java.super(SkillSpeed);
        s.setName("Speed");
        s.setDescription("For a duration boosts player's speed");
        var SkillSpeedSettings = new SkillSettings();
        SkillSpeedSettings.addNode(SkillNodes.COOLDOWN, 8000, -500);
        SkillSpeedSettings.addNode(SkillNodes.MANACOST, 350, -10);
        s.setSettings(SkillSpeedSettings);
    },
    cast: function (character, extendedSkillInfo) {
        character.sendMessage("You've used skill speed");
        return SkillResult.OK;
    }
}));
var SuperJump = new (Java.extend(ActiveSkill, {
    init: function () {
        var s = Java.super(SuperJump);
        s.setName("SuperJump");
        s.setDescription("Launches player into air");
        var SkillSpeedSettings = new SkillSettings();
        SkillSpeedSettings.addNode(SkillNodes.COOLDOWN, 8000, -500);
        SkillSpeedSettings.addNode(SkillNodes.MANACOST, 350, -10);
        SkillSpeedSettings.addNode("velocity", 30, 10);
        s.setSettings(SkillSpeedSettings);
    },
    cast: function(character, extendedSkillInfo) {
        var optional = character.getPlayer().getData(VelocityData.class);
        if (optional.isPresent()) {
            var VelocityData = velocityData.get();
            var vector = velocityData.getVelocity();
            var newVector = new Vector3d(vector);
            var i = getLevelNode(extendedSkillInfo,"velocity");
            newVector.add(0,i,0);
            velocityData.setVelocity(newVector);
            character.getPlayer().offer(VelocityData);
            character.sendMessage("You've used skill SuperJump")
            return SkillResult.OK;
        }
        return SkillResult.CANCELLED;
    }
}));
var Heal = new (Java.extend(ActiveSkill, {
    init: function() {
        var s = Java.super(Heal);
        s.setName("Heal");
        s.setDescription("After a delay heals the caster");
        var HealSettings = new SkillSettings();
        HealSettings.addNode(SkillNodes.COOLDOWN, 80000, -300);
        HealSettings.addNode("delay", 3500, -100);
        HealSettings.addNode(SkillNodes.MANACOST,150,20);
        HealSettings.addNode("healed-amount",1,1);
        HealSettings.addNode("default-regen-mult",1,0.2);
        s.setSettings(HealSettings);
    },
    cast: function(character, extendedSkillInfo) {
        GlobalScope.game.getScheduler().getTaskBuilder().delay(getLevelNode(extendedSkillInfo,"delay"), TimeUnit.MILLISECONDS).name("healtask").execute(function() {
            var healedamount = getLevelNode(extendedSkillInfo, "healed-amount") * getLevelNode(extendedSkillInfo,"default-regen-mult");
            healedamount = GlobalScope.characterService.healCharacter(character, healedamount);
            character.sendMessage("You have been healed for "+ healedamount);
        }).submit(GlobalScope.plugin);
        return SkillResult.OK;
    }

}));

registerSkill(Heal);
registerSkill(SuperJump);
registerSkill(SkillSpeed);