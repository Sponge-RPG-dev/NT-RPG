package cz.neumimto.rpg.spigot.bridges.mmoitems;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.events.character.SpigotCharacterGainedLevelEvent;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import de.tr7zw.nbtapi.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ability.Ability;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.util.*;

public class MMOItemsExpansion implements Listener {

    private static SpigotCharacterService characterService;

    private static String primaryClassType;

    @Inject
    private SpigotItemService spigotItemService;

    @Inject
    private Injector injector;

    public void init(SpigotCharacterService spigotCharacterService) {
        characterService = spigotCharacterService;
        MMOItems.plugin.setRPG(new MMOItemsRpgHandler(characterService));
        Map<String, ClassTypeDefinition> class_types = Rpg.get().getPluginConfig().CLASS_TYPES;
        int min = Integer.MAX_VALUE;

        for (Map.Entry<String, ClassTypeDefinition> next : class_types.entrySet()) {
            if (Math.min(min, next.getValue().getOrder()) == next.getValue().getOrder()) {
                primaryClassType = next.getKey();
                min = next.getValue().getOrder();
            }
        }
        spigotItemService.setItemHandler(new MMOItemStackRpgHandler());


        reloadMMOItemSkills();
    }

    public void reloadMMOItemSkills() {
        Collection<Ability> all = MMOItems.plugin.getAbilities().getAll();
        List<ISkill> iSkills = MMOItemWrapperFactory.generateSkills(all);
        for (ISkill iSkill : iSkills) {
            injector.injectMembers(iSkill);
            Rpg.get().getSkillService().registerAdditionalCatalog(iSkill);
        }
    }

    @EventHandler
    public void onLevelUp(SpigotCharacterGainedLevelEvent event) {
        PlayerData.get(event.getTarget().getUUID()).updateInventory();
    }

    //@EventHandler
    public void onCharacterChange(Void event) {
        //todo
        UUID uuid = null;
        ((MMOItemsCharacter) PlayerData.get(uuid).getRPG()).character = null;
    }


    public static class MMOItemsRpgHandler implements RPGHandler {
        private SpigotCharacterService characterService;

        public MMOItemsRpgHandler(SpigotCharacterService characterService) {
            this.characterService = characterService;
        }

        @Override
        public RPGPlayer getInfo(PlayerData playerData) {
            return new MMOItemsCharacter(playerData, playerData.getUniqueId());
        }

        @Override
        public void refreshStats(PlayerData playerData) {
            //??? what is this
        }

    }

    public static class MMOItemsCharacter extends RPGPlayer {
        private final UUID uuid;
        private ISpigotCharacter character;

        public MMOItemsCharacter(PlayerData playerData, UUID uuid) {
            super(playerData);
            this.uuid = uuid;
        }

        private ISpigotCharacter getCharacter() {
            if (character == null) {
                //todo remove
                character = characterService.getCharacter(uuid);
            }
            return character;
        }

        public int getLevel() {
            ISpigotCharacter character = getCharacter();
            if (character == null) {
                return 0;
            }
            PlayerClassData classByType = character.getClassByType(primaryClassType);
            if (classByType != null) {
                return classByType.getLevel();
            }
            return 0;
        }

        public String getClassName() {
            ISpigotCharacter character = getCharacter();
            if (character == null) {
                return "";
            }
            PlayerClassData classByType = character.getClassByType(primaryClassType);
            if (classByType != null) {
                return classByType.getClassDefinition().getName();
            }
            return "";
        }

        public double getMana() {
            return getCharacter().getMana().getValue();
        }

        public void setMana(double value) {
            this.getCharacter().getMana().setValue(value);
        }

        public double getStamina() {
            return getCharacter().getPlayer().getFoodLevel();
        }

        public void setStamina(double value) {
            getCharacter().getPlayer().setFoodLevel((int) value);
        }

    }

    public static class MMOItemStackRpgHandler extends SpigotItemService.SpigotItemHandler {
        @Override
        protected Map<String, Double> getItemData(NBTItem nbtItem) {
            Map<String, Double> map = new HashMap<>();
            Double mmoitems_attack_damage = nbtItem.getDouble("MMOITEMS_ATTACK_DAMAGE");
            if (mmoitems_attack_damage != null) {
                map.put(ItemService.DAMAGE_KEY, mmoitems_attack_damage);
            }
            return map;
        }
    }

}
