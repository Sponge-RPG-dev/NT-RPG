package cz.neumimto.rpg.bridges.itemizer;

import com.onaple.itemizer.service.IItemService;
import cz.neumimto.rpg.bridges.itemizer.factory.EffectBeanFactory;
import cz.neumimto.rpg.bridges.itemizer.factory.ItemLevelBeanFactory;
import org.spongepowered.api.Sponge;

import java.util.Optional;

public class Itemizer {

    public static void initItemizerIntegration() {
        Optional<IItemService> provide = Sponge.getServiceManager().provide(IItemService.class);
        IItemService iItemService = provide.get();
        iItemService.addThirdpartyConfig(new EffectBeanFactory());
        iItemService.addThirdpartyConfig(new ItemLevelBeanFactory());
    }
}
