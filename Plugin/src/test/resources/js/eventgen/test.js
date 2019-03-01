/*java */
var HashSet = Java.type('java.util.HashSet');
var HashMap = Java.type('java.util.HashMap')
var Runnable = Java.type("java.lang.Runnable");
var Consumer = Java.type("java.util.function.Consumer");
var events = new HashMap();


function registerEventListener(eventclass, consumer) {
    var cls = events.get(eventclass)
    if (cls == null) {
        cls = new HashSet();
        events.put(eventclass, cls);
    }
    cls.add(consumer);
}

var DamageEvent = Java.type("org.spongepowered.api.event.entity.DamageEntityEvent");
var DisplaceEntityEvent = Java.type("org.spongepowered.api.event.entity.MoveEntityEvent");

var TimeUnit = Java.type("java.util.concurrent.TimeUnit");

registerEventListener(DamageEvent, new (Java.extend(Consumer, {
    accept: function (event) {
        print(event + "- test event 1");
    }
})));
registerEventListener(DamageEvent, new (Java.extend(Consumer, {
    accept: function (event) {
        print(event + "- test event 2");
    }
})));
registerEventListener(DisplaceEntityEvent, new (Java.extend(Consumer, {
    accept: function (event) {
        print(event + "- test event 1");
    }
})));

function getMap() {
    return events;
}
