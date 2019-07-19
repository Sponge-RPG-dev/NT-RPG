package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.spigot.entities.ISpigorEntity;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import org.bukkit.entity.Player;

public interface ISpigotCharacter extends IActiveCharacter<Player, SpigotParty>, ISpigorEntity<Player>, IEntity<Player> {

}
