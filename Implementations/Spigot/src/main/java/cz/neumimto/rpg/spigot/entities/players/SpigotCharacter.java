package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SpigotCharacter extends ActiveCharacter<Player, SpigotParty> implements ISpigotEntity<Player> {

    private ISkill soedc;
    private Map<String, SpigotSkillTreeViewModel> skillTreeviewLocation = new HashMap<>();
    private boolean spellbookRotationActive = false;
    private ItemStack[][] spellbook;
    private int spellbookPage;

    public SpigotCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        super(uuid, base, propertyCount);
    }

    public void sendMessage(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        getPlayer().spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
    }

    public Map<String, SpigotSkillTreeViewModel> getSkillTreeViewLocation() {
        return skillTreeviewLocation;
    }

    public boolean isSpellRotationActive() {
        return spellbookRotationActive;
    }

    public void setSpellbook(ItemStack[][] itemStacks) {
        this.spellbook = itemStacks;
    }

    public ItemStack[][] getSpellbook() {
        return spellbook;
    }

    public void setSpellRotation(boolean active) {
        this.spellbookRotationActive = true;
    }

    public int getSpellbookPage() {
        return spellbookPage;
    }

    public void setSpellbookPage(int page) {
        this.spellbookPage = page;
    }

    @Override
    public IEntityType getType() {
        return IEntityType.CHARACTER;
    }

    @Override
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public boolean isDetached() {
        return getPlayer() == null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public SpigotSkillTreeViewModel getLastTimeInvokedSkillTreeView() {
        for (SpigotSkillTreeViewModel skillTreeViewModel : skillTreeviewLocation.values()) {
            if (skillTreeViewModel.isCurrent()) {
                return skillTreeViewModel;
            }
        }
        return null;
    }

    @Override
    public ISkill skillOrEffectDamageCause() {
        return soedc;
    }

    @Override
    public ISpigotEntity setSkillOrEffectDamageCause(ISkill rpgElement) {
        soedc = rpgElement;
        return this;
    }
}
