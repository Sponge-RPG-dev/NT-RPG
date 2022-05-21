package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

public interface ISpigotCharacter extends IActiveCharacter<Player, SpigotParty>, ISpigotEntity<Player>, IEntity<Player> {

    Player getPlayer();

    SpigotSkillTreeViewModel getLastTimeInvokedSkillTreeView();

    Map<String, SpigotSkillTreeViewModel> getSkillTreeViewLocation();

    boolean isSpellRotationActive();

    void setSpellbook(ItemStack[][] itemStacks);

    ItemStack[][] getSpellbook();

    void setSpellRotation(boolean active);

    int getSpellbookPage();

    void setSpellbookPage(int page);

    Stack<String> getGuiCommandHistory();

    void setResourceUIHandler(Consumer<ISpigotCharacter> handler);

}
