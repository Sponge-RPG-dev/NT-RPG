package cz.neumimto.rpg.spigot.gui;

import org.bukkit.entity.Player;

public interface GlyphResolver {

    String resolve(Player player, String input);
}
