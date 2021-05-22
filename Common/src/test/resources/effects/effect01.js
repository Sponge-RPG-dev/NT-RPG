var EffectBase = Java.type("cz.neumimto.rpg.api.effects.EffectBase")

var EffectBase = new (Java.extend(EffectBase, {
    onApply: function(self) {
    },
    onRemove: function(self) {
    },
    getName: function() {
        return "testEffect"
    }
}));