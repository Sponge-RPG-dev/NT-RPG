package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import cz.neumimto.rpg.spigot.gui.SpellbookListener;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static cz.neumimto.rpg.common.logging.Log.info;

@Singleton
public class SpigotCharacterService extends CharacterService<SpigotCharacter> {

    @Inject
    private ResourceService resourceService;

    public SpigotCharacterService() {
        if (SpigotRpgPlugin.isFolia()) {
            characters = new ConcurrentHashMap<>();
        } else {
            characters = new HashMap<>();
        }
    }

    @Override
    protected void initSpellbook(SpigotCharacter activeCharacter, String[][] spellbookPages) {
        activeCharacter.setSpellbook(new ItemStack[3][9]);
    }

    @Override
    protected SpigotCharacter createCharacter(UUID player, CharacterBase characterBase) {
        return new SpigotCharacter(player, characterBase, PropertyService.LAST_ID);
    }

    @Override
    protected void initSpellbook(SpigotCharacter activeCharacter, int i, int j, PlayerSkillContext skill) {
        activeCharacter.getSpellbook()[i][j] = SpigotGuiHelper.toItemStack(activeCharacter, skill);
    }

    @Override
    public SpigotPreloadCharacter buildDummyChar(UUID uuid) {
        info("Creating a dummy character for " + uuid);
        return new SpigotPreloadCharacter(uuid);
    }

    @Override
    public void registerDummyChar(SpigotCharacter dummy) {

    }

    @Override
    public boolean assignPlayerToCharacter(UUID uniqueId) {
        return false;
    }

    @Override
    public int canCreateNewCharacter(UUID uniqueId, String name) {
        return 0;
    }


    @Override
    protected void scheduleNextTick(Runnable r) {
        Bukkit.getScheduler().runTaskLater(SpigotRpgPlugin.getInstance(), r, 1L);
    }

    public SpigotCharacter getCharacter(Player target) {
        return getCharacter(target.getUniqueId());
    }

    public void setHeathscale(SpigotCharacter character, double i) {
        character.getCharacterBase().setHealthScale(i);
        character.getPlayer().setHealthScale(i);
        putInSaveQueue(character.getCharacterBase());
    }

    @Override
    public void notifyCooldown(ActiveCharacter caster, PlayerSkillContext skillContext, long cd) {
        if (cd > 0) {
            ISkill skill = skillContext.getSkill();

            if (caster instanceof SpigotCharacter) {
                SpigotCharacter character = (SpigotCharacter) caster;
                Player player = character.getPlayer();

                PlayerSkillContext skillInfo = character.getSkillInfo(skill);
                if (skillInfo == null) { //nadmin command
                    return;
                }
                String icon = skillInfo.getSkillData().getIcon();

                if (icon != null) {
                    cd /= 50;
                    Material material = Material.matchMaterial(icon);
                    player.setCooldown(material, (int) cd);
                }
            }
        }
    }

    @Override
    public void updateSpellbook(SpigotCharacter character) {
        Player player = character.getPlayer();
        InventoryView openInventory = player.getOpenInventory();
        Inventory topInventory = openInventory.getTopInventory();
        if (SpellbookListener.isInInventory(topInventory)) {
            SpellbookListener.commit(character, topInventory);
            putInSaveQueue(character.getCharacterBase());
            Gui.displayCharacterMenu(character);
        }
    }


    @Override
    public void addExperiences(SpigotCharacter character, double exp, String source) {
        if ("VANILLA".equals(source)) {
            character.getPlayer().giveExp((int) exp);
        } else {
            super.addExperiences(character, exp, source);
        }
    }
}
