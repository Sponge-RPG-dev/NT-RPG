package cz.neumimto.rpg.spigot.items;

import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.NamespacedKey;

public class RPGItemMetadataKeys {
    public static final NamespacedKey IFACE = new NamespacedKey(SpigotRpgPlugin.getInstance(), "IFACE");
    public static final NamespacedKey COMMAND = new NamespacedKey(SpigotRpgPlugin.getInstance(), "COMMAND");
    public static final NamespacedKey LEARNED_SPELL = new NamespacedKey(SpigotRpgPlugin.getInstance(), "LEARNED_SPELL");
    public static final NamespacedKey BINDICON = new NamespacedKey(SpigotRpgPlugin.getInstance(), "BINDICON");
    public static final NamespacedKey SPELLBOOKEMPTY = new NamespacedKey(SpigotRpgPlugin.getInstance(), "SPELLBOOKEMPTY");
}
