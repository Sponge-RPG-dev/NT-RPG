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

package cz.neumimto.rpg.common.effects.core;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.character.CharacterManaRegainEvent;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;

/**
 * Created by NeumimTo on 9.8.2015.
 */
@Generate(id = "name", description = "A component which enables mana regeneration")
public class DefaultManaRegeneration extends EffectBase {

    public static final String name = "DefaultManaRegen";
    private static final String apply = "You've gained mana reneneration.";
    private static final String remove = "You've lost mana regenartion.";
    private IActiveCharacter character;

    public DefaultManaRegeneration(IEffectConsumer character) {
        super(name, character);
        this.character = (IActiveCharacter) character;
        setPeriod(Rpg.get().getPluginConfig().MANA_REGENERATION_RATE);
        setApplyMessage(apply);
        setExpireMessage(remove);
        setDuration(-1);
        addEffectType(CoreEffectTypes.MANA_REGEN);
    }

    @Override
    public IEffectConsumer getConsumer() {
        return character;
    }

    @Override
    public void onApply(IEffect self) {
        Gui.sendEffectStatus(character, EffectStatusType.APPLIED, this);
    }

    @Override
    public void onRemove(IEffect self) {
        Gui.sendEffectStatus(character, EffectStatusType.EXPIRED, this);
    }

    @Override
    public void onTick(IEffect self) {
        double current = character.getMana().getValue();
        double max = character.getMana().getMaxValue();
        if (current >= max) {
            return;
        }
        double regen = character.getMana().getRegen()
                * Rpg.get().getEntityService().getEntityProperty(character, CommonProperties.mana_regen_mult);

        CharacterManaRegainEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterManaRegainEvent.class);
        event.setTarget(character);
        event.setAmount(regen);
        event.setSource(this);

        if (Rpg.get().postEvent(event)) return;

        current += event.getAmount();
        if (current > max) current = max;

        event.getTarget().getMana().setValue(current);
        Gui.displayMana(character);
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

}
