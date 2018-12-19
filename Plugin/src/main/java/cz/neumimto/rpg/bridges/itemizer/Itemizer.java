package cz.neumimto.rpg.bridges.itemizer;

import com.google.common.reflect.TypeToken;
import com.onaple.itemizer.data.beans.IItemBeanConfiguration;
import com.onaple.itemizer.events.ItemizerPreLoadEvent;
import com.onaple.itemizer.service.IItemBeanFactory;
import com.onaple.itemizer.service.IItemService;
import cz.neumimto.rpg.ResourceLoader;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;

import java.util.Optional;

@ResourceLoader.ListenerClass
public class Itemizer {

    @Listener
    public void onItemizerReady(ItemizerPreLoadEvent event) {
        Optional<IItemService> provide = Sponge.getServiceManager().provide(IItemService.class);
        IItemService iItemService = provide.get();
        iItemService.addThirdpartyConfig(new EffectBeanFactory());

    }

}
