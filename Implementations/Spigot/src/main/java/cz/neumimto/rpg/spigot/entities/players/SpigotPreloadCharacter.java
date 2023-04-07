package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.PlayerNotInGameException;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class SpigotPreloadCharacter extends SpigotCharacter {

    private Map<String, SpigotSkillTreeViewModel> skillTreeViewModelMap;

    public SpigotPreloadCharacter(UUID uuid) {
        super(uuid, new CharacterBase(), PropertyService.LAST_ID);
        skillTreeViewModelMap = new HashMap<>();
    }

    @Override
    public Player getPlayer() {
        Player player = Bukkit.getServer().getPlayer(getUUID());
        if (player != null) {
            return player;
        } else {
            throw new PlayerNotInGameException(String.format(
                    "Player object with uuid=%s has not been constructed yet. Calling PreloadCharacter.getCharacter in a wrong state", getUUID()));
        }
    }

    @Override
    public Resource getResource(String name) {
        return null;
    }

    @Override
    public void addResource(String name, Resource resource) {

    }

    @Override
    public SpigotSkillTreeViewModel getLastTimeInvokedSkillTreeView() {
        return null;
    }

    @Override
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public Map<String, SpigotSkillTreeViewModel> getSkillTreeViewLocation() {
        return skillTreeViewModelMap;
    }

    @Override
    public boolean isSpellRotationActive() {
        return false;
    }

    @Override
    public void setSpellbook(ItemStack[][] itemStacks) {

    }

    @Override
    public ItemStack[][] getSpellbook() {
        return new ItemStack[0][];
    }

    @Override
    public void setSpellRotation(boolean active) {

    }

    @Override
    public int getSpellbookPage() {
        return 1;
    }

    @Override
    public void setSpellbookPage(int page) {

    }

    @Override
    public Stack<String> getGuiCommandHistory() {
        return new Stack<>();
    }

    @Override
    public void removeResource(String type) {

    }

    @Override
    public Map<String, Integer> getAttributesTransaction() {
        return Collections.emptyMap();
    }

    @Override
    public void setAttributesTransaction(HashMap<String, Integer> map) {

    }

    @Override
    public Map<String, PlayerSkillContext> getSkillsByName() {
        return Collections.emptyMap();
    }


    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }
    @Override
    public String getPlayerAccountName() {
        return getPlayer().getName();
    }


}
