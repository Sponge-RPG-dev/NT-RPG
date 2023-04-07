package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

class SpigotResourceViewUpdater extends BukkitRunnable {

    Consumer<SpigotCharacter> uiHandlerFactory;

    public SpigotResourceViewUpdater(Consumer<SpigotCharacter> uiHandlerFactory) {
        this.uiHandlerFactory = uiHandlerFactory;
    }

    @Override
    public void run() {
        Collection<ActiveCharacter> characters = Rpg.get().getCharacterService().getCharacters();
        for (ActiveCharacter character : characters) {
            uiHandlerFactory.accept((SpigotCharacter) character);
        }
    }

}
