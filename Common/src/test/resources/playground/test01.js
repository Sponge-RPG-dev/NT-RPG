var type = Java.type("cz.neumimto.rpg.jsplayground.PLType")
var typeval = Java.type("cz.neumimto.rpg.jsplayground.PLTypeValue")

var mytype = Java.extend(type);
var myType2 = Java.extend(type);

var array = [];
array[0] = mytype;
array[1] = mytype;

var type3 = Java.extend(type, {

});
array[2] = type3;