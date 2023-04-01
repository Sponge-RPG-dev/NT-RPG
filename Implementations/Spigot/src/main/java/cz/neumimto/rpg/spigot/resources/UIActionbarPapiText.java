package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class UIActionbarPapiText implements Consumer<SpigotCharacter> {

    public static String pattern;

    private static DatapackManager datapackManager;

    public static void init(ResourceGui resourceGui) {
        pattern = resourceGui.pattern;

        datapackManager = SpigotRpgPlugin.getInjector().getInstance(DatapackManager.class);
    }

    @Override
    public void accept(SpigotCharacter character) {
        Player player = character.getEntity();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        Component component = datapackManager.resolveGlyphs(player, pattern);
        player.sendActionBar(component);
    }
}
