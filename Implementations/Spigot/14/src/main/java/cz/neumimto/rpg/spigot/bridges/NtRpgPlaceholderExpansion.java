package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.leveling.ILevelProgression;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class NtRpgPlaceholderExpansion extends PlaceholderExpansion {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private PluginConfig pluginConfig;

    @Override
    public String getIdentifier() {
        return "ntrpg";
    }

    @Override
    public String getAuthor() {
        return "NeumimTo";
    }

    @Override
    public String getVersion(){
        return SpigotRpgPlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }


    /**
     * ntrpg_character_name - returns character name
     * ntrpg_character_property_((property)) - returns character property value (ei max health, max mana, mana regen, fire resistance etc...)
     * ntrpg_character_class_((class_type)) - returns name of character class of specific type - (ntrpg_character_class_Race - returns name of character race, or null if player has none)
     * ntrpg_character_class_level_((class_type)) - returns level of character class of specific type - (ntrpg_character_class_level_Race - returns level of character race, or null player do not have that class)
     * ntrpg_character_class_exp_((class_type)) - returns experiences of character class of specific type from the begging of the level - (ntrpg_character_class_level_Race - returns level of character race, or null player do not have that class)
     * ntrpg_character_class_exp_exptreshold_((class_type)) - returns experiences of character class of specific type from the begging of the level - (ntrpg_character_class_level_Race - returns level of character race, or null player do not have that class)
     *
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }


        ISpigotCharacter character = characterService.getCharacter(player);
        if(identifier.equals("character_name")){
            return character.getName();
        }

        if (identifier.startsWith("character_")) {
            String substring = identifier.substring(10);
            if (substring.startsWith("property_")) {
                substring = substring.substring(9);
                int idByName = propertyService.getIdByName(substring);
                return String.valueOf(character.getProperty(idByName));
            }

            if (substring.startsWith("class_")) {
                substring = substring.substring(6);

                if (substring.startsWith("level_")) {
                    substring = substring.substring(6);
                    PlayerClassData classByType = character.getClassByType(substring);
                    if (classByType != null) {
                        return String.valueOf(classByType.getCharacterClass().getLevel());
                    }
                }

                if (substring.startsWith("exp_")) {
                    substring = substring.substring(4);
                    PlayerClassData classByType = character.getClassByType(substring);
                    if (classByType != null) {
                        return String.valueOf(classByType.getCharacterClass().getExperiences());
                    }
                }

                if (substring.startsWith("exptreshold_")) {
                    substring = substring.substring(12);
                    PlayerClassData classByType = character.getClassByType(substring);
                    if (classByType != null) {
                        int lvl = classByType.getCharacterClass().getLevel();
                        ILevelProgression levelProgression = classByType.getClassDefinition().getLevelProgression();
                        if (levelProgression != null) {
                            return String.valueOf(levelProgression.getLevelMargins()[lvl]);
                        }
                    }
                }

                PlayerClassData classByType = character.getClassByType(substring);
                if (classByType != null) {
                    return classByType.getClassDefinition().getName();
                }
                return null;
            }

        }

        return null;
    }
}
