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

import cz.neumimto.config.blackjack.and.hookers.annotations.AsCollectionImpl;
import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import cz.neumimto.config.blackjack.and.hookers.annotations.Default;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.effects.EffectSourceType;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.leveling.EmptyLevelProgression;
import cz.neumimto.rpg.common.entity.players.leveling.ILevelProgression;
import cz.neumimto.rpg.common.entity.players.leveling.SkillTreeType;
import cz.neumimto.rpg.sponge.configuration.adapters.*;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.*;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@ConfigSerializable
public class ClassDefinition implements IEffectSourceProvider {

    @Setting("Name")
    protected String name;

    @Setting("Description")
    protected String description;

    @Setting("WelcomeMessage")
    protected String welcomeMessage;

    @Setting("PreferredTextColor")
    protected String preferedColor;

    @Setting("ItemType")
    protected String itemType;

    @Setting("Visible")
    protected boolean showsInMenu = true;

    @Setting("ClassType")
    @CustomAdapter(ClassTypeAdapter.class)
    protected String type;

    @Setting("Properties")
    @CustomAdapter(PropertiesArrayAdapter.class)
    protected float[] propBonus;

    @Setting("AllowedArmor")
    @CustomAdapter(ClassRpgItemTypeAdapter.class)
    protected Set<ClassItem> allowedArmor = new HashSet<>();

    @Setting("Permissions")
    @AsCollectionImpl(TreeSet.class)
    protected Set<PlayerClassPermission> permissions;

    @Setting("PropertiesLevelBonus")
    @CustomAdapter(PropertiesArrayAdapter.class)
    protected float[] propLevelBonus;

    @Setting("ExitCommands")
    @AsCollectionImpl(ArrayList.class)
    protected List<String> exitCommands;

    @Setting("EnterCommands")
    @AsCollectionImpl(ArrayList.class)
    protected List<String> enterCommands;

    @Setting("ProjectileDamage")
    protected Map<String, Double> projectileDamage = new HashMap<>();

    @Setting("Weapons")
    @CustomAdapter(ClassRpgItemTypeAdapter.class)
    protected Set<ClassItem> weapons = new HashSet<>();

    @Setting("Attributes")
    @CustomAdapter(AttributeMapAdapter.class)
    protected Map<AttributeConfig, Integer> startingAttributes = new HashMap<>();

    @Setting("Effects")
    @CustomAdapter(EffectsAdapter.class)
    protected Map<IGlobalEffect, EffectParams> effects = new HashMap<>();

    @Setting("Offhand")
    @CustomAdapter(ClassRpgItemTypeAdapter.class)
    protected Set<ClassItem> offHandWeapons = new HashSet<>();

    @Setting("Experiences")
    @CustomAdapter(ClassExpAdapter.class)
    protected Map<String, Map<String, Double>> experiences = new HashMap<>();

    @Setting("SkillTreeId")
    @CustomAdapter(SkillTreeLookupAdapter.class)
    protected SkillTree skillTree;

    @Setting("SkillPointsPerLevel")
    protected int skillpointsPerLevel;

    @Setting("AttributePointsPerLevel")
    protected int attributepointsPerLevel;

    @Setting("Leveling")
    @Default(EmptyLevelProgression.class)
    protected ILevelProgression levels;

    @Setting("SkillTreeType")
    protected SkillTreeType skillTreeType;

    @Setting("ExperienceSources")
    @AsCollectionImpl(HashSet.class)
    protected Set<String> experienceSourceSet;

    @Setting("Default")
    protected boolean defaultClass;

    @Setting("Dependencies")
    @CustomAdapter(ClassDependencyGraphAdapter.class)
    protected DependencyGraph classDefinitionDependencyGraph;

    @Setting("CustomLore")
    @AsCollectionImpl(ArrayList.class)
    protected List<String> customLore;

    public ClassDefinition(String name, String classType) {
        this.name = name;
        this.type = classType;
        this.classDefinitionDependencyGraph = new DependencyGraph(this);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    @Override
    public String toString() {
        return "ClassDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
