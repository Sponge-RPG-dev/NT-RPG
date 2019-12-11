package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import org.bukkit.entity.Player;

public interface ISpigotCharacter extends IActiveCharacter<Player, SpigotParty>, ISpigotEntity<Player>, IEntity<Player> {


    Player getPlayer();
}
