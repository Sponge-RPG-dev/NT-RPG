package cz.neumimto.rpg;

import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;
import org.spongepowered.api.Sponge;

import java.util.Collection;
import java.util.Optional;

public final class SpongeRpgApi implements RpgApi {

    protected SpongeRpgApi() {
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return Sponge.getRegistry().getAllOf(Attribute.class);
    }

    @Override
    public Optional<SkillPreProcessorFactory> getSkillPreProcessorFactory(String preprocessorFactoryId) {
        return Sponge.getRegistry().getType(SkillPreProcessorFactory.class, preprocessorFactoryId);
    }

    @Override
    public ItemService getItemService() {
        return NtRpgPlugin.GlobalScope.itemService;
    }
}
