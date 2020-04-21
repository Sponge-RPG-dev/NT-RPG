package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.events.character.SpigotCharacterGainedLevelEvent;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

public class MMOItemsExpansion implements Listener {

    private static SpigotCharacterService characterService;

    private static String primaryClassType;

    public void init(SpigotCharacterService spigotCharacterService) {
        characterService = spigotCharacterService;
        MMOItems.plugin.setRPG(new MMOItemsRpgHandler(characterService));
        Map<String, ClassTypeDefinition> class_types = Rpg.get().getPluginConfig().CLASS_TYPES;
        int min = Integer.MAX_VALUE;

        Iterator<Map.Entry<String, ClassTypeDefinition>> iterator = class_types.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClassTypeDefinition> next = iterator.next();
            if (Math.min(min, next.getValue().getOrder()) == next.getValue().getOrder()) {
                primaryClassType = next.getKey();
                min = next.getValue().getOrder();
            }
            iterator.next();
        }
    }

    @EventHandler
    public void onLevelUp(SpigotCharacterGainedLevelEvent event) {
        PlayerData.get(event.getTarget().getUUID()).scheduleDelayedInventoryUpdate();
    }

    public static class MMOItemsRpgHandler implements RPGHandler {
        private SpigotCharacterService characterService;

        public MMOItemsRpgHandler(SpigotCharacterService characterService) {
            this.characterService = characterService;
        }

        @Override
        public RPGPlayer getInfo(PlayerData playerData) {
            return new MMOItemsCharacter(playerData, characterService.getCharacter(playerData.getUniqueId()));
        }

        @Override
        public void refreshStats(PlayerData playerData) {
            //??? what is this
        }

    }

    public static class MMOItemsCharacter extends RPGPlayer {
        private final ISpigotCharacter character;

        public MMOItemsCharacter(PlayerData playerData, ISpigotCharacter character) {
            super(playerData);
            this.character = character;
        }

        public int getLevel() {
            PlayerClassData classByType = character.getClassByType(primaryClassType);
            if (classByType != null) {
                return classByType.getLevel();
            }
            return 0;
        }

        public String getClassName() {
            PlayerClassData classByType = character.getClassByType(primaryClassType);
            if (classByType != null) {
                return classByType.getClassDefinition().getName();
            }
            return "";
        }

        public double getMana() {
            return character.getMana().getValue();
        }

        public void setMana(double value) {
            this.character.getMana().setValue(value);
        }

        public double getStamina() {
            return character.getPlayer().getFoodLevel();
        }

        public void setStamina(double value) {
            character.getPlayer().setFoodLevel((int)value);
        }

        @Override
        public boolean canCast(AbilityData data, boolean message) {

            IActiveCharacter character = characterService.getCharacter(getPlayer());
            if (character.isSilenced()) {
                String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.PLAYER_SILENCED);
                character.sendMessage(translate);
                return false;
            }

            float manacostReduce = character.getProperty(CommonProperties.mana_cost_reduce);

            //MMOITEMS start
            double remaining = getPlayerData().getRemainingAbilityCooldown(data.getAbility());
            if (remaining <= 0.0D) {
                if (MMOItems.plugin.getConfig().getBoolean("permissions.abilities") && !getPlayer().hasPermission("mmoitems.ability." + data.getAbility().getLowerCaseID()) && !getPlayer().hasPermission("mmoitems.bypass.ability")) {
                    return false;
                    //RPG start
                } else if (data.hasModifier("mana") && this.getMana() < data.getModifier("mana") * manacostReduce) {
                    Message.NOT_ENOUGH_MANA.format(ChatColor.RED, new String[0]).send(getPlayer(), "not-enough-mana");
                    //RPG end
                    return false;
                } else if (data.hasModifier("stamina") && this.getStamina() < data.getModifier("stamina")) {
                    Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED, new String[0]).send(getPlayer(), "not-enough-stamina");
                    return false;
                } else {
                    return true;
                }
            } else {
                if (message) {
                    String progressBar = ChatColor.YELLOW + "";
                    double cooldown = data.getModifier("cooldown");
                    double progress = (cooldown - remaining) / cooldown * 10.0D;
                    String barChar = MMOItems.plugin.getConfig().getString("cooldown-progress-bar-char");

                    for(int j = 0; j < 10; ++j) {
                        progressBar = progressBar + (progress >= (double)j ? ChatColor.GREEN : ChatColor.WHITE) + barChar;
                    }

                    Message.SPELL_ON_COOLDOWN.format(ChatColor.RED, new String[]{"#left#", "" + (new DecimalFormat("0.#")).format(remaining), "#progress#", progressBar, "#s#", remaining >= 2.0D ? "s" : ""}).send(getPlayer(), "ability-cooldown");
                }

                return false;
            }
            //MMOITEMS end
        }
    }

}
