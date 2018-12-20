package cz.neumimto.rpg.bridges.itemizer;

import com.onaple.itemizer.events.ItemizerPreLoadEvent;
import com.onaple.itemizer.service.IItemBeanFactory;
import com.onaple.itemizer.service.IItemService;
import cz.neumimto.rpg.Log;
import cz.neumimto.rpg.ResourceLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ResourceLoader.ListenerClass
public class Itemizer {

    public static Set<Class<? extends IItemBeanFactory>> itemBeanFactories = new HashSet<>();

    @Listener
    public void onItemizerReady(ItemizerPreLoadEvent event) {
        Optional<IItemService> provide = Sponge.getServiceManager().provide(IItemService.class);
        IItemService iItemService = provide.get();
        for (Class<? extends IItemBeanFactory> itemBeanFactory : itemBeanFactories) {
            try {
                iItemService.addThirdpartyConfig(itemBeanFactory.getConstructor().newInstance());
                Log.info("ItemizerBridge: registered " + itemBeanFactory.getSimpleName());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Log.error("Could not create itemizer bridge for " + itemBeanFactory.getSimpleName(), e);
            }
        }
    }

}
