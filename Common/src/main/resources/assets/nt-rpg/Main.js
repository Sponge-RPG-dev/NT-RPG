var console = Java.type("java.lang.System").out;
/*java */
var ArrayList = Java.type('java.util.ArrayList');
var HashMap = Java.type('java.util.HashMap');
/* https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions */

let events = new ArrayList();
let globalEffects = new ArrayList();
let skillHandlers = new HashMap();

let log = function(obj) {
    console.println("[NTRPG-JS]" + obj);
}

let registerGlobalEffect = function(obj) {
    globalEffects.add(obj);
}

let registerSkillHandler = function(id,obj) {
    if (skillHandlers.containsKey(id)) {
        log("Multiple scripts attempted to register skill handler id " + id + " will be skipped.")
    } else {
        skillHandlers.put(id,obj);
    }
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
    events.add(eventData);
}

var lib = {
    getEventListeners: function() {
        return events;
    },
    getGlobalEffects: function() {
        return globalEffects;
    },
    getSkillHandlers: function() {
        log(skillHandlers.size());
        return skillHandlers;
    }
}
