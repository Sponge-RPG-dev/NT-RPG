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

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.ISkillType;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.annotations.CatalogId;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by NeumimTo on 12.3.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public abstract class AbstractSkill<T> implements ISkill<T> {

    private static final String SKILL = "skill";
    public static final String PLAYER = "player";

    @Inject
    protected LocalizationService localizationService;

    protected SkillSettings settings = new SkillSettings();

    @CatalogId
    private String catalogId;

    private Set<ISkillType> skillTypes = new HashSet<>();
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
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE,
                    IActiveCharacter.getName(), context.getSkillData().getSkillName());
        }
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE,
                    Arg.arg(PLAYER, IActiveCharacter.getName())
                            .with(SKILL, context.getSkillData().getSkillName())
                            .with("level", level));
        }
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE,
                    Arg.arg(PLAYER, IActiveCharacter.getName())
                            .with(SKILL, context.getSkillData().getSkillName()));
        }
    }

    @Override
    public SkillSettings getDefaultSkillSettings() {
        return settings;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().SKILLGAIN_MESSAGES_AFTER_LOGIN) {
            String msg = localizationService.translate(LocalizationKeys.PLAYER_GAINED_SKILL,
                    Arg.arg("skill", context.getSkillData().getSkillName()));
            c.sendMessage(msg);
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
    public Set<ISkillType> getSkillTypes() {
        return skillTypes;
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
    protected void finalize() throws Throwable {
        super.finalize();
        Log.info(Console.PURPLE + "Destroying " + getId() + " classloader: " + getClass().getClassLoader().toString(), DebugLevel.DEVELOP);
    }

}
