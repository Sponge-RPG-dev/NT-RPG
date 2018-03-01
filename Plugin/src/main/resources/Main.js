var imports = new JavaImporter(java.util, java.nio.file);
var console = Java.type("java.lang.System").out;
/*java */
var HashSet = Java.type('java.util.HashSet');
var ArrayList = Java.type('java.util.ArrayList');
var HashMap = Java.type('java.util.HashMap');
var File = Java.type("java.io.File");
var TimeUnit = Java.type("java.util.concurrent.TimeUnit");
var Runnable = Java.type("java.lang.Runnable");
var Consumer = Java.type("java.util.function.Consumer");
/*plugin */
/*TODO bind from plugin */
var SkillSettings = Java.type("cz.neumimto.rpg.skills.SkillSettings");
var SkillNodes = Java.type("cz.neumimto.rpg.skills.SkillNodes");
var ActiveSkill = Java.type("cz.neumimto.rpg.skills.ActiveSkill");
var SkillResult = Java.type("cz.neumimto.rpg.skills.SkillResult");
var AbstractSkill = Java.type("cz.neumimto.rpg.skills.AbstractSkill");
var PassiveSkill = Java.type("cz.neumimto.rpg.skills.PassiveSkill");
var GlobalEffect = Java.type("cz.neumimto.rpg.effects.IGlobalEffect");
var EffectBase = Java.type("cz.neumimto.rpg.effects.EffectBase");
var PluginConfig = Java.type("cz.neumimto.rpg.configuration.PluginConfig");
var Effect = Java.type("cz.neumimto.rpg.effects.EffectBase");
var SpeedBoost = Java.type("cz.neumimto.rpg.effects.common.positive.SpeedBoost");
var DefaultProperties = Java.type("cz.neumimto.rpg.players.properties.DefaultProperties");
var JSLoader = Java.type("cz.neumimto.rpg.scripting.JSLoader");
var CharacterAttribute = Java.type("cz.neumimto.rpg.players.properties.attributes.CharacterAttribute");
/* sponge */
var Texts = Java.type("org.spongepowered.api.text.Text");
var Keys = Java.type("org.spongepowered.api.data.key.Keys");
/* libs */
var Vector3d = Java.type("com.flowpowered.math.vector.Vector3d");
var Optional = Java.type("com.google.common.base.Optional");
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

var events = new HashMap();
var skills = new ArrayList();
var globalEffects = new ArrayList();
var attributes = new ArrayList();
//
function log(obj) {
    console.println("[NTRPG-JS]" + obj);
}

function registerSkill(obj) {
    skills.add(obj);
}

function registerGlobalEffect(obj) {
    globalEffects.add(obj);
}

function registerAttribute(obj) {
    attributes.add(obj);
}

function defineCharacterProperty(name, def) {
    var lastid = playerPropertyService.LAST_ID;
    lastid++;
    if (name !== null) {
        playerPropertyService.registerProperty(name, lastid);
    }
    if (def !== null) {
        playerPropertyService.registerDefaultValue(lastid, def);
    }
    playerPropertyService.LAST_ID = lastid;
    return lastid;
}

function getLevelNode(extendedSkillInfo, node) {
    return extendedSkillInfo.getSkillData().getSkillSettings().getLevelNodeValue(node, extendedSkillInfo.getLevel());
}

function registerEventListener(eventclass, consumer) {
    var cls = events.get(eventclass);
    if (cls == null) {
        cls = new HashSet();
        events.put(eventclass, cls);
    }
    cls.add(consumer);
}
with (imports) {
    var stream = Files.newDirectoryStream(new File("./config/nt-rpg/scripts").toPath(), "*.js");
    stream.forEach(function (p) {
        var name = p.toFile().absolutePath;
        if (!name.endsWith("Main.js")) {
            load(name);
        }
    });
}

function registerAttributes() {
    log("registerAttributes, " + attributes.size())
    for (obj in attributes) {
        var a = attributes.get(obj);
        if (a instanceof CharacterAttribute) {
            GlobalScope.propertyService.registerAttribute(a);
        }
    }
}

function registerSkills() {
    log("registerSkills, " + skills.size())


    for (obj in skills) {
        var s = skills.get(obj);
        s.init();
        GlobalScope.skillService.addSkill(s);
    }
}


function registerGlobalEffects() {
    log("registerGlobalEffects, " + globalEffects.size())
    for (obj in globalEffects) {
        var g = globalEffects.get(obj);
        if (g instanceof GlobalEffect) {
            GlobalScope.effectService.registerGlobalEffect(g);
        }
    }
}

function generateListener() {
    if (!events.isEmpty()) {
        log("generateListener")
        IoC.build(JSLoader.class).generateDynamicListener(events);
    }
    events.clear();
}