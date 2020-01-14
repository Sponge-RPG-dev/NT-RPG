package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import org.bukkit.entity.Player;

import java.util.Map;

public interface ISpigotCharacter extends IActiveCharacter<Player, SpigotParty>, ISpigotEntity<Player>, IEntity<Player> {

    Player getPlayer();

    SpigotSkillTreeViewModel getLastTimeInvokedSkillTreeView();

    Map<String, SpigotSkillTreeViewModel> getSkillTreeViewLocation();

}
