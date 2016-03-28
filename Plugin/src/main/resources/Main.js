var imports = new JavaImporter(java.util,java.nio.file);
/*java */
var HashSet = Java.type('java.util.HashSet');
var HashMap = Java.type('java.util.HashMap')
var File = Java.type("java.io.File");
var TimeUnit = Java.type("java.util.concurrent.TimeUnit");
var Runnable = Java.type("java.lang.Runnable");
var Consumer = Java.type("java.util.function.Consumer");
/*plugin */
var SkillSettings = Java.type("cz.neumimto.rpg.skills.SkillSettings");
var SkillNodes = Java.type("cz.neumimto.rpg.skills.SkillNode");
var ActiveSkill = Java.type("cz.neumimto.rpg.skills.ActiveSkill");
var SkillResult =  Java.type("cz.neumimto.rpg.skills.SkillResult");
var AbstractSkill = Java.type("cz.neumimto.rpg.skills.AbstractSkill");
var GlobalEffect = Java.type("cz.neumimto.rpg.effects.IGlobalEffect");
var EffectBase = Java.type("cz.neumimto.rpg.effects.EffectBase");
var PluginConfig = Java.type("cz.neumimto.rpg.configuration.PluginConfig");
var Effect = Java.type("cz.neumimto.rpg.effects.EffectBase");
var SpeedBoost = Java.type("cz.neumimto.rpg.effects.common.positive.SpeedBoost");
/* sponge */
var Texts = Java.type("org.spongepowered.api.text.Text");
var Keys = Java.type("org.spongepowered.api.data.key.Keys");
/* libs */
var Vector3d = Java.type("com.flowpowered.math.vector.Vector3d");
var Optional = Java.type("com.google.common.base.Optional");
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

var events = new HashMap();

function registerSkill(obj) {
    if (obj instanceof AbstractSkill) {
        if (typeof obj.init == 'function') {
            obj.init();
        }
        GlobalScope.skillService.addSkill(obj);
    }
}
function registerGlobalEffect(obj) {
    if (obj instanceof GlobalEffect) {
        GlobalScope.effectService.registerGlobalEffect(obj);
    }
}
function defineCharacterProperty(name,def) {
    var lastid = playerPropertyService.LAST_ID;
    lastid++;
    if (name !== null) {
        playerPropertyService.registerProperty(name,lastid);
    }
    if (def !== null) {
        playerPropertyService.registerDefaultValue(lastid,def);
    }
    playerPropertyService.LAST_ID = lastid;
    return lastid;
}
function getLevelNode(extendedSkillInfo,node) {
    return extendedSkillInfo.getSkillInfo().getSkillSettings().getLevelNodeValue(node,extendedSkillInfo.getLevel());
}
function registerEventListener(eventclass,consumer) {
    var cls = events.get(eventclass)
    if (cls == null) {
        cls = new HashSet();
        events.put(eventclass,new HashSet());
    }
    cls.add(consumer);
}
with (imports) {
    var stream = Files.newDirectoryStream(new File("./mods/NtRpg/scripts").toPath(),"*.js");
    stream.forEach(function(p) {
        var name = p.toFile().absolutePath;
        if (!name.endsWith("Main.js")) {
            load(name);
        }
    });
}

if (!events.isEmpty()) {
    IoC.build(cz.neumimto.scripting.JSLoader).generateDynamicListener(events);
}
events.clear();