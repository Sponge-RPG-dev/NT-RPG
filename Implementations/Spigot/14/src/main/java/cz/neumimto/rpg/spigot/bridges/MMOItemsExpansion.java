package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.spigot.bridges.mmoitems.MMOItemWrapperFactory;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.events.character.SpigotCharacterGainedLevelEvent;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import de.tr7zw.nbtapi.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ability.Ability;
import net.Indyuce.mmoitems.api.player.CooldownInformation;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.*;

public class MMOItemsExpansion implements Listener {

    private static SpigotCharacterService characterService;

    private static String primaryClassType;

    @Inject
    private SpigotItemService spigotItemService;


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
            Rpg.get().getSkillService().registerAdditionalCatalog(iSkill);
        }
    }

    @EventHandler
    public void onLevelUp(SpigotCharacterGainedLevelEvent event) {
        PlayerData.get(event.getTarget().getUUID()).scheduleDelayedInventoryUpdate();
    }

    //@EventHandler
    public void onCharacterChange(Void event) {
        //todo
        UUID uuid = null;
        ((MMOItemsCharacter)PlayerData.get(uuid).getRPG()).character = null;
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
            getCharacter().getPlayer().setFoodLevel((int)value);
        }

        @Override
        public boolean canCast(AbilityData data, boolean message) {
            //mmoitems start
            if (getPlayerData().hasCooldownInfo(data.getAbility())) {
                CooldownInformation info = getPlayerData().getCooldownInfo(data.getAbility());
                if (!info.hasCooledDown()) {
                    if (message) {
                        String progressBar = ChatColor.YELLOW + "";
                        double progress = (info.getInitialCooldown() - info.getRemaining()) / info.getInitialCooldown() * 10.0D;
                        String barChar = MMOItems.plugin.getConfig().getString("cooldown-progress-bar-char");

                        for(int j = 0; j < 10; ++j) {
                            progressBar = progressBar + (progress >= (double)j ? ChatColor.GREEN : ChatColor.WHITE) + barChar;
                        }

                        Message.SPELL_ON_COOLDOWN.format(ChatColor.RED, new String[]{"#left#", "" + (new DecimalFormat("0.#")).format(info.getRemaining()), "#progress#", progressBar, "#s#", info.getRemaining() >= 2.0D ? "s" : ""}).send(getPlayer(), "ability-cooldown");
                    }

                    return false;
                }
            }
            //mmoitewms end

            float manacostReduce = character.getProperty(CommonProperties.mana_cost_reduce);

            if (MMOItems.plugin.getConfig().getBoolean("permissions.abilities") && !getPlayer().hasPermission("mmoitems.ability." + data.getAbility().getLowerCaseID()) && !getPlayer().hasPermission("mmoitems.bypass.ability")) {
                return false;
                //ntrpg start
            } else if (data.hasModifier("mana") && this.getMana() < data.getModifier("mana") * manacostReduce) {
                Message.NOT_ENOUGH_MANA.format(ChatColor.RED, new String[0]).send(getPlayer(), "not-enough-mana");
                return false;
                //ntrpg end
            } else if (data.hasModifier("stamina") && this.getStamina() < data.getModifier("stamina")) {
                Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED, new String[0]).send(getPlayer(), "not-enough-stamina");
                return false;
            } else {
                return true;
            }
            //mmoitems end
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
