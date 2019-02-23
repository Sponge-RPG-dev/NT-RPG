package cz.neumimto.rpg.utils;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import cz.neumimto.rpg.players.leveling.SkillTreeType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigSerializable
public class EditorMappings {

    @Setting("TextColors")
    public List<TextColor> textColors;

    @Setting("Armor")
    public List<String> armor;

    @Setting("Properties")
    public List<String> properties;

    @Setting("WeaponClasses")
    public List<String> weaponClasses;

    @Setting("ClassTypes")
    public Set<String> classTypes;

    @Setting("Weapons")
    public Set<String> weapons;

    @Setting("SkillTreeTypes")
    public Set<String> skillTreeTypes;

    public static void dump() {
        EditorMappings editorMappings = new EditorMappings();
        editorMappings.textColors = new ArrayList<>(Sponge.getRegistry().getAllOf(TextColor.class));
        editorMappings.armor = NtRpgPlugin.GlobalScope.itemService.getArmorList().stream().map(RPGItemType::toConfigString).collect(Collectors.toList());
        editorMappings.properties = new ArrayList<>(NtRpgPlugin.GlobalScope.propertyService.getAllProperties());
        editorMappings.weaponClasses = NtRpgPlugin.GlobalScope.itemService.getWeaponClasses().stream().map(WeaponClass::getName).map(a -> "WeaponClass:" + a).collect(Collectors.toList());
        editorMappings.classTypes = NtRpgPlugin.pluginConfig.CLASS_TYPES.keySet();
        editorMappings.weapons = NtRpgPlugin.GlobalScope.itemService.getRegisteredWeapons();
        editorMappings.skillTreeTypes = Stream.of(SkillTreeType.values()).map(Enum::name).collect(Collectors.toSet());


        FileUtils.generateConfigFile(editorMappings, new File(NtRpgPlugin.workingDir, "EditorMappings.conf"));
    }
}
