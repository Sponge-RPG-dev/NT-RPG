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
/* sponge */
var Texts = Java.type("org.spongepowered.api.text.Text");
var Keys = Java.type("org.spongepowered.api.data.key.Keys");
/* libs */
var Vector3d = Java.type("com.flowpowered.math.vector.Vector3d");
var Optional = Java.type("com.google.common.base.Optional");
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

/* Also available:
var Folder // java.nio.file.Path of scripts folder
var GlobalScope // GlobalScope, containing characterService, effectService, entityService and others.
See https://github.com/Sponge-RPG-dev/NT-RPG/blob/master/Plugin/src/main/java/cz/neumimto/rpg/GlobalScope.java
 */

var events = new ArrayList();

var skills = new ArrayList();
var globalEffects = new ArrayList();
var attributes = new ArrayList();

var folder = Folder;

with (imports) {
    Files.walkFileTree(folder, new (Java.extend(Java.type("java.nio.file.SimpleFileVisitor"), {
        visitFile: function (file, attrs) {
            if (file.toString().endsWith(".js") && !file.toString().endsWith("Main.js")) {
                load(file.toString());
            }
            return FileVisitResult.CONTINUE;
        }
    })));
}

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

function registerEventListener(eventData) {
    if (eventData == null) {
        log("Could not register Event listener defined via JS, parametr EventData is null")
        return;
    }
    if (eventData.consumer == null) {
        log("Could not register Event listener defined via JS, parametr EventData.consumer is null")
        return;
    }
    if (eventData.type == null) {
        log("Could not register Event listener defined via JS, parametr EventData.type is null")
        return;
    }
    events.add(eventData);
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
        GlobalScope.skillService.registerAdditionalCatalog(s);
    }
}


function registerGlobalEffects() {
    log("registerGlobalEffects, " + globalEffects.size())
    for (obj in globalEffects) {
        var g = globalEffects.get(obj);
        if (g instanceof Java.type("cz.neumimto.rpg.effects.IGlobalEffect")) {
            GlobalScope.effectService.registerGlobalEffect(g);
        }
    }
}

function generateListener() {
    if (!events.isEmpty()) {
        log("generateListener")
        GlobalScope.jsLoader.generateDynamicListener(events);
    }
    events.clear();
}