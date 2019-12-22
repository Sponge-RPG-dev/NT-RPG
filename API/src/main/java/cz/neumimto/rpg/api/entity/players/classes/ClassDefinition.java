/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.api.entity.players.classes;

import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecValidator;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.adapters.*;
import cz.neumimto.rpg.api.effects.*;
import cz.neumimto.rpg.api.entity.players.leveling.ILevelProgression;
import cz.neumimto.rpg.api.entity.players.leveling.SkillTreeType;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

import java.util.*;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class ClassDefinition implements IEffectSourceProvider {

    @Path("Name")
    protected String name;

    @Path("Description")
    protected List<String> description;

    @Path("WelcomeMessage")
    protected String welcomeMessage;

    @Path("PreferredTextColor")
    protected String preferedColor;

    @Path("ItemType")
    protected String itemType;

    @Path("Visible")
    @Conversion(BooleanConverter.class)
    protected boolean showsInMenu = true;

    @Path("ClassType")
    @SpecValidator(ClassTypeAdapter.class)
    protected String type;

    @Path("Properties")
    @Conversion(PropertiesArrayAdapter.class)
    protected float[] propBonus;

    @Path("AllowedArmor")
    @Conversion(ClassRpgItemTypeAdapter.class)
    protected Set<ClassItem> allowedArmor = new HashSet<>();

    @Path("Permissions")
    @Conversion(ClassPermissionAdapter.class)
    protected Set<PlayerClassPermission> permissions;

    @Path("PropertiesLevelBonus")
    @Conversion(PropertiesArrayAdapter.class)
    protected float[] propLevelBonus;

    @Path("ExitCommands")
    protected List<String> exitCommands;

    @Path("EnterCommands")
    protected List<String> enterCommands;

    @Path("ProjectileDamage")
    @Conversion(MapStringDoubleAdapter.class)
    protected Map<String, Double> projectileDamage = new HashMap<>();

    @Path("Weapons")
    @Conversion(ClassRpgItemTypeAdapter.class)
    protected Set<ClassItem> weapons = new HashSet<>();

    @Path("Attributes")
    @Conversion(AttributeMapAdapter.class)
    protected Map<AttributeConfig, Integer> startingAttributes = new HashMap<>();

    @Path("Effects")
    @Conversion(EffectsAdapter.class)
    protected Map<IGlobalEffect, EffectParams> effects = new HashMap<>();

    @Path("Offhand")
    @Conversion(ClassRpgItemTypeAdapter.class)
    protected Set<ClassItem> offHandWeapons = new HashSet<>();

    @Path("Experiences")
    @Conversion(DimExperiencesAdapter.class)
    protected Map<String, Map<String, Double>> experiences = new HashMap<>();

    @Path("SkillTreeId")
    @Conversion(SkillTreeLookupAdapter.class)
    protected SkillTree skillTree;

    @Path("SkillPointsPerLevel")
    protected int skillpointsPerLevel;

    @Path("AttributePointsPerLevel")
    protected int attributepointsPerLevel;

    @Path("Leveling")
    @Conversion(LevelProgressionConverter.class)
    protected ILevelProgression levels;

    @Path("SkillTreeType")
    protected SkillTreeType skillTreeType;

    @Path("ExperienceSources")
    @Conversion(StringSet.class)
    protected Set<String> experienceSourceSet;

  //  @Path("Dependencies")
//    @Conversion(ClassDependencyGraphAdapter.class)
    protected transient DependencyGraph classDefinitionDependencyGraph;

    @Path("CustomLore")
    protected List<String> customLore;

    public ClassDefinition(String name, String classType) {
        this.name = name;
        this.type = classType;
        this.classDefinitionDependencyGraph = new DependencyGraph(this);
        this.experienceSourceSet = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public boolean showsInMenu() {
        return showsInMenu;
    }

    public float[] getPropBonus() {
        return propBonus;
    }

    public boolean isShowsInMenu() {
        return showsInMenu;
    }

    public Set<ClassItem> getAllowedArmor() {
        return allowedArmor;
    }

    public Set<ClassItem> getWeapons() {
        return weapons;
    }

    public Set<ClassItem> getOffHandWeapons() {
        return offHandWeapons;
    }

    public Set<PlayerClassPermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(Set<PlayerClassPermission> permissions) {
        this.permissions = new TreeSet<>(permissions);
    }

    public float[] getPropLevelBonus() {
        return propLevelBonus;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Map<AttributeConfig, Integer> getStartingAttributes() {
        return startingAttributes;
    }

    public String getClassType() {
        return type;
    }

    public Map<IGlobalEffect, EffectParams> getEffects() {
        return effects;
    }

    public void setEffects(Map<IGlobalEffect, EffectParams> effects) {
        this.effects = effects;
    }

    public Map<String, Double> getProjectileDamage() {
        return projectileDamage;
    }

    public List<String> getExitCommands() {
        return exitCommands;
    }

    public List<String> getEnterCommands() {
        return enterCommands;
    }

    public String getPreferedColor() {
        return preferedColor;
    }

    public double getExperiencesBonus(String dimmension, String type) {
        Map<String, Double> entityTypeDoubleMap = getExperiences().get(dimmension);
        if (entityTypeDoubleMap == null) {
            return 0;
        }
        Double aDouble = entityTypeDoubleMap.get(type);
        return aDouble == null ? 0 : aDouble;
    }

    public Map<String, Map<String, Double>> getExperiences() {
        return experiences;
    }

    public DependencyGraph getClassDependencyGraph() {
        return classDefinitionDependencyGraph;
    }

    public boolean hasExperienceSource(String source) {
        return experienceSourceSet.contains(source);
    }

    public void addExperienceSource(String source) {
        experienceSourceSet.add(source);
    }

    public List<String> getCustomLore() {
        return customLore;
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public void setSkillTree(SkillTree skillTree) {
        this.skillTree = skillTree;
    }

    public int getSkillpointsPerLevel() {
        return skillpointsPerLevel;
    }

    public int getAttributepointsPerLevel() {
        return attributepointsPerLevel;
    }

    @Override
    public IEffectSource getType() {
        return EffectSourceType.CLASS;
    }

    public ILevelProgression getLevelProgression() {
        return levels;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public SkillTreeType getSkillTreeType() {
        return skillTreeType;
    }

    public Set<String> getExperienceSource() {
        return experienceSourceSet;
    }

    public void setExperienceSources(Set<String> expU) {
        this.experienceSourceSet = expU;
    }

    @Override
    public String toString() {
        return "ClassDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }


    private static class BooleanConverter implements Converter<Boolean, Boolean> {

        @Override
        public Boolean convertToField(Boolean value) {
            if (value == null) {
                return false;
            }
            return value;
        }

        @Override
        public Boolean convertFromField(Boolean value) {
            return value;
        }
    }

    private static class StringSet implements Converter<Set<String>, List<String>> {
        @Override
        public Set<String> convertToField(List<String> value) {
            return new HashSet<>(value);
        }

        @Override
        public List<String> convertFromField(Set<String> value) {
            return new ArrayList<>(value);
        }
    }
}
