package cz.neumimto.rpg.inventory.runewords;

import cz.neumimto.rpg.inventory.SocketType;
import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class Rune extends ItemUpgrade {




	@Override
	public SocketType getSocketType() {
		return SocketType.RUNE;
	}
}
