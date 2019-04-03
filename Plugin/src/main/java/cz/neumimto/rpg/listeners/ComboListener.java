package cz.neumimto.rpg.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.inventory.UserActionType;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import javax.inject.Inject;

@Singleton
@ResourceLoader.ListenerClass
public class ComboListener {

	@Inject
	private CharacterService characterService;

	@Listener
	@Include({
			InteractItemEvent.Secondary.MainHand.class,
	})
	public void onRMBClick(InteractEvent e, @Root Player player) {
		if (player.get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
			IActiveCharacter character = characterService.getCharacter(player);
			e.setCancelled(characterService.processUserAction(character, UserActionType.R));
		}
	}

	@Listener
	@Include({
			InteractItemEvent.Primary.MainHand.class
	})
	public void onLMBClick(InteractEvent e, @Root Player player) {
		if (player.get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
			IActiveCharacter character = characterService.getCharacter(player);
			e.setCancelled(characterService.processUserAction(character, UserActionType.L));
		}
	}

	@Listener(order = Order.EARLY)
	public void onQPress(DropItemEvent.Pre e, @Root Player player) {
		if (player.get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
			IActiveCharacter character = characterService.getCharacter(player);
			e.setCancelled(characterService.processUserAction(character, UserActionType.Q));
		}
	}

	@Listener(order = Order.EARLY)
	public void onInventoryOpen(InteractInventoryEvent.Open e, @Root Player player) {
		if (player.get(Keys.GAME_MODE).get() == GameModes.SURVIVAL) {
			IActiveCharacter character = characterService.getCharacter(player);
			e.setCancelled(characterService.processUserAction(character, UserActionType.E));
		}
	}

}
