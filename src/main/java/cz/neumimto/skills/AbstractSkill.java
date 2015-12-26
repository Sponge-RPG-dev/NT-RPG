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

package cz.neumimto.skills;

import cz.neumimto.configuration.Localization;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by NeumimTo on 12.3.2015.
 */
public abstract class AbstractSkill implements ISkill {
    protected String name;
    protected String description;
    protected SkillSettings settings;
    private Set<SkillType> skillTypes = new HashSet<>();
    protected SkillItemIcon icon;
    protected URL url;
    private String lore;
    private DamageType damagetype;

    public AbstractSkill() {
        icon = new SkillItemIcon(this);
    }

    @Inject
    protected Game game;

    @Inject
    protected CharacterService characterService;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter) {
        if (PluginConfig.PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE) {
            Text t = Texts.of(Localization.PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE.replace("%1", IActiveCharacter.getName()).replace("%2", getName()));
            game.getServer().getOnlinePlayers().stream().forEach(p -> p.sendMessage(t));
        }
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
        if (PluginConfig.PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE) {
            Text t = Texts.of(Localization.PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE.replace("%1", IActiveCharacter.getName()).replace("%2", getName()).replace("%3", level + ""));
            game.getServer().getOnlinePlayers().stream().forEach(p -> p.sendMessage(t));
        }
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter) {
        if (PluginConfig.PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE) {
            Text t = Texts.of(Localization.PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE.replace("%1", IActiveCharacter.getName()).replace("%2", getName()));
            game.getServer().getOnlinePlayers().stream().forEach(p -> p.sendMessage(t));
        }
    }

    @Override
    public SkillSettings getDefaultSkillSettings() {
        return settings;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {
        if (PluginConfig.SKILLGAIN_MESSAGES_AFTER_LOGIN) {
            c.sendMessage("You've gained skill" + getName() + " level: " + level);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Set<SkillType> getSkillTypes() {
        return skillTypes;
    }

    @Override
    public boolean showsToPlayers() {
        return true;
    }

    @Override
    public SkillItemIcon getIcon() {
        return icon;
    }

    @Override
    public URL getIconURL() {
        return url;
    }

    @Override
    public void setIconURL(URL url) {
        this.url = url;
    }

    @Override
    public String getLore() {
        return lore;
    }

    @Override
    public DamageType getDamageType() {
        return damagetype;
    }

    @Override
    public void setDamageType(DamageType type) {
        damagetype = type;
    }
}
