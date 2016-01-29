var imports = new JavaImporter(java.util,java.nio.file);
/*java */
var HashSet = Java.type('java.util.HashSet');
var File = Java.type("java.io.File");
var TimeUnit = Java.type("java.util.concurrent.TimeUnit")
var Runnable = Java.type("java.lang.Runnable");
/*plugin */
var SkillSettings = Java.type("cz.neumimto.skills.SkillSettings");
var SkillNodes = Java.type("cz.neumimto.skills.SkillNode");
var ActiveSkill = Java.type("cz.neumimto.skills.ActiveSkill");
var SkillResult =  Java.type("cz.neumimto.skills.SkillResult");
var AbstractSkill = Java.type("cz.neumimto.skills.AbstractSkill");
var GlobalEffect = Java.type("cz.neumimto.effects.IGlobalEffect");
var EffectBase = Java.type("cz.neumimto.effects.EffectBase");
var PluginConfig = Java.type("cz.neumimto.configuration.PluginConfig");
var Effect = Java.type("cz.neumimto.effects.EffectBase");
var SpeedBoost = Java.type("cz.neumimto.effects.common.positive.SpeedBoost");
/* sponge */
var Texts = Java.type("org.spongepowered.api.text.Text");
var Keys = Java.type("org.spongepowered.api.data.key.Keys");
/* libs */
var Vector3d = Java.type("com.flowpowered.math.vector.Vector3d");
var Optional = Java.type("com.google.common.base.Optional");
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

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
with (imports) {
    var stream = Files.newDirectoryStream(new File("./mods/NtRpg/scripts").toPath(),"*.js");
    stream.forEach(function(p) {
        var name = p.toFile().absolutePath;
        if (!name.endsWith("Main.js")) {
            load(name);
        }
    });
}