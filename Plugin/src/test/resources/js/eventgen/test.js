/*java */
var HashSet = Java.type('java.util.HashSet');
var HashMap = Java.type('java.util.HashMap')
var ArrayList = Java.type('java.util.ArrayList')
var Runnable = Java.type("java.lang.Runnable");
var Consumer = Java.type("java.util.function.Consumer");
var events = new ArrayList();


function registerEventListener(eventData) {
    events.add(eventData);
}

var DamageEvent = Java.type("org.spongepowered.api.event.entity.DamageEntityEvent");
var DisplaceEntityEvent = Java.type("org.spongepowered.api.event.entity.MoveEntityEvent");

var TimeUnit = Java.type("java.util.concurrent.TimeUnit");

var test = new (Java.extend(Consumer, {
               accept: function (event) {
                   print(event + "- test event 1");
               }
           }));
var test2 = {type: DamageEvent, consumer: test};

registerEventListener(test2);

registerEventListener({type: DamageEvent, consumer: new (Java.extend(Consumer, {
    accept: function (event) {
        print(event + "- test event 2");
    }
})), beforeModifications: true});

registerEventListener({type: DisplaceEntityEvent, consumer: new (Java.extend(Consumer, {
    accept: function (event) {
        print(event + "- test event 1");
    }
})), order: "LATE"});

function getMap() {
    return events;
}
