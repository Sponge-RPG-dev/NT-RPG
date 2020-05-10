registerSkillHandler({
    onCast: function(caster, context) {
        caster.add("Test")
    }
})

registerEventListener(
    {
        type: "org.spongepowered.api.event.network.ClientConnectionEvent",
        consumer: function(event) {
                log(event);
        },
        order: "BEFORE_POST",
        beforeModifications: false
    }
);
