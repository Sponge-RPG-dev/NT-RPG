var console = Java.type("java.lang.System").out;
/*java */
var ArrayList = Java.type('java.util.ArrayList');
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

/* Also available:
var Folder // java.nio.file.Path of scripts folder
var Bindings //
var Rpg // Rpg, containing characterService, effectService, entityService and others.
 */

let events = new ArrayList();
let globalEffects = new ArrayList();
let skillHandlers = new ArrayList();

let log = function(obj) {
    console.println("[NTRPG-JS]" + obj);
}

let registerGlobalEffect = function(obj) {
    globalEffects.add(obj);
}

let registerSkillHandler = function(obj) {
    log("registered skill handler")
    skillHandlers.add(obj);
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
    if (eventData.type() == null) {
        log("Could not register Event listener defined via JS, parametr EventData.type is null")
        return;
    }
    log("registered event listener")
    events.add(eventData);
}
    print("Loading" + toLoad);
for (var k in toLoad) {
    var w = toLoad[k];
    print("Loading" + w);
    load(w)
}


print(events.size())
var lib = {
    getEventListeners: function() {
        return events;
    },
    getGlobalEffects: function() {
        return globalEffects;
    },
    getSkillHandlers: function() {
        return skillHandlers;
    }
}
print(lib.getEventListeners().size())