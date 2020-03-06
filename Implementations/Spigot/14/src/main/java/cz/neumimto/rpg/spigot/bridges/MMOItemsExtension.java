package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;

import javax.inject.Inject;

public class MMOItemsExtension {

    @Inject
    private SpigotCharacterService characterService;

    public void init() {
        MMOItems.plugin.setRPG(new MMOItemsRpgHandler());
    }

    public static class MMOItemsRpgHandler implements RPGHandler {

        @Override
        public RPGPlayer getInfo(PlayerData playerData) {
            return null;
        }

        @Override
        public void refreshStats(PlayerData playerData) {

        }
    }
}
