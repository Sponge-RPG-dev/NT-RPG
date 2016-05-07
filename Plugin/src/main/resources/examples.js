var SkillSpeed = new (Java.extend(ActiveSkill, {
    init: function () {
        var s = Java.super(SkillSpeed);
        s.setName("Speed");
        s.setDescription("For a duration boosts player's speed");
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
        GlobalScope.effectService.addEffect(speedEffect, character);
        return SkillResult.OK;
    }
}));
var SkillBloodMagic = new (Java.extend(PassiveSkill, {
    init: function () {
        var s = java.super(SkillBloodMagic);
        s.setName("BloodMagic");
        s.setDescription("All skills will require life instead of mana");
        var SkillBloodMagicSettings = new SkillSettings();
        SkillBloodMagicSettings.addNode();
        s.setSettings(SkillBloodMagicSettings);
    },
    applyEffect: function (character, extendedSkillInfo) {
        GlobalScope.effectService.addEffect(new BloodMagicEffect(character))
    }
}))
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
    cast: function (character, extendedSkillInfo) {
        var optional = character.getPlayer().getData(VelocityData.class);
        if (optional.isPresent()) {
            var VelocityData = velocityData.get();
            var vector = velocityData.getVelocity();
            var newVector = new Vector3d(vector);
            var i = getLevelNode(extendedSkillInfo, "velocity");
            newVector.add(0, i, 0);
            velocityData.setVelocity(newVector);
            character.getPlayer().offer(VelocityData);
            character.sendMessage("You've used skill SuperJump");
            return SkillResult.OK;
        }
        return SkillResult.CANCELLED;
    }
}));
var Heal = new (Java.extend(ActiveSkill, {
    init: function () {
        var s = Java.super(Heal);
        s.setName("Heal");
        s.setDescription("After a delay heals the caster");
        var HealSettings = new SkillSettings();
        HealSettings.addNode(SkillNodes.COOLDOWN, 80000, -300);
        HealSettings.addNode("delay", 3500, -100);
        HealSettings.addNode(SkillNodes.MANACOST, 150, 20);
        HealSettings.addNode("healed-amount", 1, 1);
        HealSettings.addNode("default-regen-mult", 1, 0.2);
        s.setSettings(HealSettings);
    },
    cast: function (character, extendedSkillInfo) {
        GlobalScope.game.getScheduler().getTaskBuilder().delay(getLevelNode(extendedSkillInfo, "delay"), TimeUnit.MILLISECONDS).name("healtask").execute(function () {
            var healedamount = getLevelNode(extendedSkillInfo, "healed-amount") * getLevelNode(extendedSkillInfo, "default-regen-mult");
            healedamount = GlobalScope.characterService.healCharacter(character, healedamount);
            character.sendMessage("You have been healed for " + healedamount);
        }).submit(GlobalScope.plugin);
        return SkillResult.OK;
    }
}));
registerSkill(Heal);
registerSkill(SuperJump);
registerSkill(SkillSpeed);
registerSkill(SkillBloodMagic);

var Strength = new (Java.extend(CharacterAttribute));
Strength.setName("Strength");
Strength.setDescription("Some desc");
//Each point of stregth increases axe damage by 1.25
Strength.getAffectsProperties().put(DefaultProperties.diamond_axe_bonus_damage, 1.25);
Strength.getAffectsProperties().put(DefaultProperties.golden_axe_bonus_damage, 1.25);
Strength.getAffectsProperties().put(DefaultProperties.iron_axe_bonus_damage, 1.25);
Strength.getAffectsProperties().put(DefaultProperties.wooden_axe_bonus_damage, 1.25);
//register the object into game
GlobalScope.playerPropertyService.registerAttribute(Strength);

var Inteligence = new (Java.extend(CharacterAttribute));
Inteligence.setName("Inteligence");
Inteligence.setDescription("Int desc");
Inteligence.getAffectsProperties().put(DefaultProperties.max_mana, 20.0);
Inteligence.getAffectsProperties().put(DefaultProperties.mana_regen, 1.12);
GlobalScope.playerPropertyService.registerAttribute(Inteligence);

var Agility = new (Java.extend(CharacterAttribute));
Agility.setName("Agility");
Agility.setDescription("Agi desc");
/* be careful with maximum walk speed, entities with high walk speed values may cause lag or map damage
 Walk speed values around 4-5 may result in an unplayable gameplay, players wont be simply able to control their character.
 */
Agility.getAffectsProperties().put(DefaultProperties.walk_speed, 0.0075);

GlobalScope.playerPropertyService.registerAttribute(Agility);

registerEventListener(Java.type('org.spongepowered.api.event.entity.DamageEntityEvent'), new (Java.extend(Consumer, {
    accept: function (event) {
        System.out.println("Im Javascript event handler");
    }
})));

var MiningEffect = Java.extend(EffectBase, {
    getName: function () {
        return "MiningEffect"
    }

});
var Mining = new (Java.extend(PassiveSkill, {
    init: function () {
        var s = Java.super(Mining);
        s.setName("Mining");
        s.setDescription("Chance to get more resources from minerals");
        var HealSettings = new SkillSettings();
        HealSettings.addNode("chance", 0.5, 0.5);
        HealSettings.addNode("bonus-amount", 1, 0.19);
        s.setSettings(HealSettings);
    },
    applyEffect: function (character, extendedSkillInfo) {
        GlobalScope.effectService.addEffect(new MiningEffect(character))
    }
}));