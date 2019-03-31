package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.Log;
import cz.neumimto.rpg.properties.PropertyService;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.pipeline.SkillComponent;

import java.util.function.BiConsumer;

@JsBinding(JsBinding.Type.OBJECT)
@SkillComponent(
        value = "Changes default value of a property. Must be called during pre initialization (within scripts folder) in order to have any affect. ",
        usage = "set_property_default_value(\"my_property_mult\", 0);",
        params = {
                @SkillComponent.Param("string - property name"),
                @SkillComponent.Param("float - default value")
        }
)
public class Set_Property_Default_Value implements BiConsumer<String, Float> {

    @Override
    public void accept(String s, Float aFloat) {
        PropertyService build = IoC.get().build(PropertyService.class);
        try {
            build.overrideMaxPropertyValue(s, aFloat);
        } catch (Throwable t) {
            Log.error("Unknown property value "+s+ "." + " Use one of [" + String.join(", ", build.getAllProperties()) + "] ; Reloading your script wont have any effect on online players");
        }


    }
}
