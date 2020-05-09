registerSkillHandler({
    onCast: function(caster, context) {
        caster.add("Test")
    }
})

registerEventListener(
    {
        type: function() {
            return "org.spongepowered.api.event.network.ClientConnectionEvent";
        },
        consumer: function() {
            return function() {
                log(event);
            }
        },
        order: function() {
            return "BEFORE_POST";
        },
        beforeModifications: function() {
            return false;
        }
    }
);
