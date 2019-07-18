package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
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
@Singleton
public class Set_Property_Default_Value implements BiConsumer<String, Float> {

    @Inject
    private IPropertyService propertyService;

    @Override
    public void accept(String s, Float aFloat) {
        try {
            propertyService.overrideMaxPropertyValue(s, aFloat);
        } catch (Throwable t) {
            Log.error("Unknown property value " + s + "." + " Use one of [" + String.join(", ", propertyService.getAllProperties()) + "] ; Reloading your script wont have any effect on online players");
        }
    }
}
