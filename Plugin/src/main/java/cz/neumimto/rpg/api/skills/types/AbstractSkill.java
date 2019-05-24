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

package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.common.utils.annotations.CatalogId;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;


/**
 * Created by NeumimTo on 12.3.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public abstract class AbstractSkill implements ISkill {

    @Inject
    protected CharacterService characterService;

    protected String strName;
    protected List<String> description;
    protected SkillSettings settings = new SkillSettings();

    protected String itemType;

    @CatalogId
    private String catalogId;

    private Set<ISkillType> skillTypes = new HashSet<>();
    private List<String> lore;
    private String damageType = null;

    public AbstractSkill() {
        ResourceLoader.Skill sk = this.getClass().getAnnotation(ResourceLoader.Skill.class);
        if (sk != null) {
            catalogId = sk.value().toLowerCase();
        }
    }

    /**
     * Sets catalog id, if null.
     *
     * @param catalogId
     * @throws IllegalStateException if catalogId not null
     */
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    @Override
    public String getName() {
        return strName;
    }

    @Override
    public String getLocalizableName() {
        return strName;
    }

    @Override
    public void setLocalizableName(String name) {
        this.strName = name;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter) {
        if (pluginConfig.PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE, IActiveCharacter.getName(), getLocalizableName());
        }
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
        if (pluginConfig.PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE,
                    Arg.arg("player", IActiveCharacter.getName())
                            .with("skill", getName())
                            .with("level", level));
        }
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter) {
        if (pluginConfig.PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE,
                    Arg.arg("%player%", IActiveCharacter.getName())
                            .with("skill", getName()));
        }
    }

    @Override
    public SkillSettings getDefaultSkillSettings() {
        return settings;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {
        if (pluginConfig.SKILLGAIN_MESSAGES_AFTER_LOGIN) {
            c.sendMessage(Localizations.PLAYER_GAINED_SKILL, Arg.arg("skill", getName()));
        }
    }

    @Override
    public void init() {
    }

    @Override
    public SkillSettings getSettings() {
        return settings;
    }

    @Override
    public void setSettings(SkillSettings settings) {
        this.settings = settings;
    }

    @Override
    public List<String> getDescription() {
        return description;
    }

    @Override
    public void setDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public Set<ISkillType> getSkillTypes() {
        return skillTypes;
    }

    @Override
    public void setIcon(String icon) {
        this.itemType = icon;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public String getDamageType() {
        return damageType;
    }

    @Override
    public void setDamageType(String type) {
        damageType = type;
    }

    public void addSkillType(ISkillType type) {
        if (skillTypes == null) {
            skillTypes = new HashSet<>();
        }
        skillTypes.add(type);
    }

    /* Skills are singletons */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return catalogId.hashCode() * 77;
    }

    @Override
    public String getId() {
        return catalogId;
    }

    @Override
    public String getItemType() {
        return itemType;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.info(Console.PURPLE + "Destroying " + getId() + " classloader: " + getClass().getClassLoader().toString(), DebugLevel.DEVELOP);
    }
}
