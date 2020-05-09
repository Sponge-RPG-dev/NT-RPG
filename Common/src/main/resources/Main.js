var imports = new JavaImporter(java.util, java.nio.file);
var console = Java.type("java.lang.System").out;
/*java */
var ArrayList = Java.type('java.util.ArrayList');
var File = Java.type("java.io.File");
var TimeUnit = Java.type("java.util.concurrent.TimeUnit");
var Runnable = Java.type("java.lang.Runnable");
var Consumer = Java.type("java.util.function.Consumer");
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

/* Also available:
var Folder // java.nio.file.Path of scripts folder
var Bindings //
var Rpg // Rpg, containing characterService, effectService, entityService and others.
 */

let folder = Folder;

var userCtx = {
    events: new ArrayList(),
    skills: new ArrayList(),
    globalEffects: new ArrayList(),

    getEvents: function() {
        return events;
    }

    getSkills: function() {
        return skills;
    }

    getGlobalEffects: function() {
        return globalEffects;
    }
};

let log = function(obj) {
    console.println("[NTRPG-JS]" + obj);
}

let registerSkill = function(obj) {
    obj.init();
    userCtx.skills.add(obj);
}

let registerGlobalEffect = function registerGlobalEffect(obj) {
    userCtx.globalEffects.add(obj);
}

let defineCharacterProperty = function(name, def) {
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

let registerEventListener = function(eventData) {
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
    userCtx.events.add(eventData);
}

with (imports) {
    Files.walkFileTree(folder, new (Java.extend(Java.type("java.nio.file.SimpleFileVisitor"), {
        visitFile: function (file, attrs) {
            try {
                if (file.toString().endsWith(".js") && !file.toString().endsWith("Main.js")) {
                    load(file.toString());
                }
            } catch (e) {
                e.printStackTrace();
            }
            return FileVisitResult.CONTINUE;
        }
    })));
}