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

package cz.neumimto.effects.common.def;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.EffectSource;
import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.events.character.ManaRegainEvent;
import cz.neumimto.gui.Gui;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public class ManaRegeneration extends EffectBase {

    IActiveCharacter character;
    private static final String apply = "Your mana is regenerating.";
    private static final String remove = "You've lost mana regenartion.";
    public static final String name = "DefaultRegen";

    public ManaRegeneration(IActiveCharacter character) {
        super(name, character);
        this.character = character;
        setPeriod(PluginConfig.MANA_REGENERATION_RATE);
        setApplyMessage(apply);
        setExpireMessage(remove);
        setDuration(-1);
    }


    @Override
    public void onApply() {
        Gui.sendEffectStatus(character, EffectStatusType.APPLIED, this);
    }

    @Override
    public void onRemove() {
        Gui.sendEffectStatus(character, EffectStatusType.EXPIRED, this);
    }

    @Override
    public void onTick() {
        double current = character.getMana().getValue();
        double max = character.getMana().getMaxValue();
        if (current == max)
            return;
        double regen = character.getMana().getRegen()
                + character.getCharacterProperty(DefaultProperties.mana_regen_mult) * character.getLevel();
        current += regen;
        ManaRegainEvent event = new ManaRegainEvent(character);
        if (current > max) {
            event.setNewVal(max);
            event.setAmount(max - current);
        } else {
            event.setNewVal(current);
            event.setAmount(regen);
        }
        NtRpgPlugin.GlobalScope.game.getEventManager().post(event);
        event.getCharacter().getMana().setValue(event.getNewVal());
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public void setLevel(int level) {

    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public boolean setStackable(boolean b) {
        return false;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public EffectSource getEffectSource() {
        return EffectSource.DEFAULT;
    }


}
