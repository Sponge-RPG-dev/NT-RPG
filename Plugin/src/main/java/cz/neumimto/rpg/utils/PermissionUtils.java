package cz.neumimto.rpg.utils;

import cz.neumimto.rpg.configuration.PluginConfig;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderRegistration;

import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;

public class PermissionUtils {

	private static boolean luckPermsInstalled;

	static {
		try {
			Class.forName("me.lucko.luckperms.api.LuckPermsApi");
			luckPermsInstalled = true;
		} catch (ClassNotFoundException e) {
			luckPermsInstalled = false;
		}
	}

	public static int getMaximalCharacterLimit(UUID player) {
		if (!luckPermsInstalled) {
			return PluginConfig.PLAYER_MAX_CHARS;
		}
		ProviderRegistration<LuckPermsApi> provider = Sponge.getServiceManager().getRegistration(LuckPermsApi.class).get();
		LuckPermsApi api = provider.getProvider();
		User user = api.getUser(player);
		SortedSet<? extends Node> permissions = user.getPermissions();
		for (Node permission : permissions) {
			if (permission.getPermission().startsWith("ntrpg.override.char-limit.")) {
				try {
					return Integer.parseInt(permission.getPermission().substring(26));
				} catch (NumberFormatException e) {
					System.out.println("Player uuid=" + player + " has a permission node " + permission.getPermission()
							+ ", last part is expected to be an integer.");
					return PluginConfig.PLAYER_MAX_CHARS;
				}
			}
		}
		return PluginConfig.PLAYER_MAX_CHARS;
	}
}
