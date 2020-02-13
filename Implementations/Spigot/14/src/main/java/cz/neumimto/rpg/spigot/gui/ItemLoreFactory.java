package cz.neumimto.rpg.spigot.gui;


import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.ChatColor;

import java.util.*;

public class ItemLoreFactory {

    public static String JOINT = ChatColor.DARK_GRAY + "[" + ChatColor.RESET +  ChatColor.DARK_RED + "+" + ChatColor.RESET + ChatColor.DARK_GRAY + "]";
    public static String HEADER_START = ChatColor.DARK_GRAY + "════════ [ " + ChatColor.RESET + ChatColor.GREEN;
    public static String HEADER_END = ChatColor.RESET.toString() + ChatColor.DARK_GRAY + " ] ════════";
    public static String VERTICAL_LINE = ChatColor.DARK_GRAY + "║ " + ChatColor.GRAY;

    public String header(String header) {
        return JOINT + HEADER_START + header + HEADER_END;
    }

    public String line(String line) {
        return VERTICAL_LINE + line;
    }

    public String node(String key, String value) {
        return VERTICAL_LINE + ChatColor.GRAY + Rpg.get().getLocalizationService().translate(key) + ChatColor.DARK_GRAY + ": " + value;
    }

    public List<String> toLore(ClassDefinition a) {
        List<String> lore = new ArrayList<>();
        lore.add(header(ChatColor.valueOf(a.getPreferedColor()) + a.getName()));
        lore.add(node(LocalizationKeys.CLASS_TYPE, a.getClassType()));

        if (a.getDescription() != null && a.getDescription().size() > 0) {
            List<String> description = a.getDescription();
            String descriptionS = Rpg.get().getLocalizationService().translate(LocalizationKeys.DESCRIPTION);
            lore.add(header(ChatColor.GREEN + descriptionS));

            for (String s : description) {
                lore.add(line(s));
            }
        }

        if (a.getCustomLore() != null && a.getCustomLore().size() > 0) {
            String loreH = Rpg.get().getLocalizationService().translate(LocalizationKeys.LORE);
            lore.add(header(ChatColor.GREEN + loreH));

            List<String> ll = a.getCustomLore();
            for (String s : ll) {
                lore.add(line(s));
            }

        }

        return lore;
    }


    public List<String> toLore(ISpigotCharacter character, SkillData skillData, ChatColor nameColor, SkillTree skillTree) {
        ISkill skill = skillData.getSkill();
        List<String> lore = new ArrayList<>();
        if (skillData.useDescriptionOnly()) {
            List<String> description = skillData.getDescription(character);
            lore.addAll(description);
        } else {
            LocalizationService locService = Rpg.get().getLocalizationService();
            lore.add(header(nameColor + skill.getName()));
            lore.add(node(locService.translate(LocalizationKeys.SKILL_EXECUTION_TYPE), locService.translate(skill.getSkillExecutionType().toString().toLowerCase())));

            lore.add(header(character.getName()));

            PlayerSkillContext psc = character.getSkillInfo(skill);
            String level = psc == null ? " -- " : psc.getLevel() + (psc.getLevel() != psc.getTotalLevel() ? " ("+psc.getTotalLevel()+")" :"");
            lore.add(node(locService.translate(LocalizationKeys.LEVEL), level));
            lore.add(node(locService.translate(LocalizationKeys.SKILL_MAX_LEVEL), ""+ skillData.getMaxSkillLevel()));
            lore.add(node(locService.translate(LocalizationKeys.SKILL_MIN_CLASS_LEVEL), "" + skillData.getMinPlayerLevel()));
            if (skillData.getLevelGap() > 0) {
                lore.add(node(locService.translate(LocalizationKeys.SKILL_LEVEL_GAP), "" + skillData.getLevelGap()));
            }


            SkillSettings skillSettings = skillData.getSkillSettings();
            lore.add(header(locService.translate(LocalizationKeys.SKILL_SETTINGS)));

            for (Map.Entry<String, Float> entry : skillSettings.getNodes().entrySet()) {
                lore.add(node(locService.translate(entry.getKey()), String.format("%.2f", entry.getValue())));
            }

            List<String> description = skillData.getDescription(character);
            if (description != null && description.size() > 0) {
                lore.add(header(locService.translate(LocalizationKeys.DESCRIPTION)));

                for (String s : description) {
                    lore.add(line(s));
                }
            }

            lore.add(header(locService.translate(LocalizationKeys.SKILL_TRAITS)));
            Set<ISkillType> skillTypes = skill.getSkillTypes();
            StringBuilder builder = new StringBuilder();
            Iterator<ISkillType> iterator = skillTypes.iterator();
            int i = 0;
            boolean firstLine = true;
            while (iterator.hasNext()) {
                i++;
                ISkillType next = iterator.next();
                String translate = locService.translate(next.toString());
                builder.append(translate);
                if (i % 4 == 0) {
                    if (firstLine) {
                        lore.add(node(locService.translate(LocalizationKeys.SKILL_TYPES), builder.toString()));
                    } else {
                        lore.add(line(" - " + builder.toString()));
                    }

                    builder = new StringBuilder();
                    firstLine = false;
                }
            }
        }


        return lore;
    }
}
