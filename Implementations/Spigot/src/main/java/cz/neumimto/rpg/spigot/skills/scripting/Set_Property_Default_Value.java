package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.BiConsumer;

@JsBinding(JsBinding.Type.OBJECT)
@Singleton
public class Set_Property_Default_Value implements BiConsumer<String, Float> {

    @Inject
    private PropertyService propertyService;

    @Override
    public void accept(String s, Float aFloat) {
        try {
            propertyService.overrideMaxPropertyValue(s, aFloat);
        } catch (Throwable t) {
            Log.error("Unknown property value " + s + "." + " Use one of [" + String.join(", ", propertyService.getAllProperties()) + "] ; Reloading your script wont have any effect on online players");
        }
    }
}
