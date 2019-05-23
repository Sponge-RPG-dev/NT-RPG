package cz.neumimto.rpg;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

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

    private void broadcastMessage(Text text) {
        Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();
    }

    @Override
    public void broadcastMessage(String message) {
        broadcastMessage(TextHelper.parse(message));
    }

    @Override
    public void broadcastLocalizableMessage(String message, Arg arg) {
        broadcastMessage(TextHelper.parse(NtRpgPlugin.GlobalScope.localizationService.translate(message, arg)));
    }

    @Override
    public void broadcastLocalizableMessage(String message, String singleKey, String singleArg) {
        broadcastMessage(TextHelper.parse(NtRpgPlugin.GlobalScope.localizationService.translate(message, singleKey, singleArg)));
    }
}
