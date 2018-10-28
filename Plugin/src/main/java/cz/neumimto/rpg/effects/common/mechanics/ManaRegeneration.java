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

package cz.neumimto.rpg.effects.common.mechanics;

import cz.neumimto.rpg.NtRpgPlugin;
import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import cz.neumimto.rpg.effects.CoreEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.character.ManaRegainEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 9.8.2015.
 */
@Generate(id = "name", description = "A component which enables mana regeneration")
public class ManaRegeneration extends EffectBase {

	public static final String name = "DefaultManaRegen";
	private static final String apply = "You've gained mana reneneration.";
	private static final String remove = "You've lost mana regenartion.";
	IActiveCharacter character;

	public ManaRegeneration(IEffectConsumer character, long duration, Void value) {
		super(name, character);
		this.character = (IActiveCharacter) character;
		setPeriod(pluginConfig.MANA_REGENERATION_RATE);
		setApplyMessage(apply);
		setExpireMessage(remove);
		setDuration(-1);
		effectTypes.add(CoreEffectTypes.MANA_REGEN);
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
		if (current >= max) {
			return;
		}
		double regen = character.getMana().getRegen()
				+ getGlobalScope().characterService.getCharacterProperty(character, DefaultProperties.mana_regen_mult)
				* character.getLevel();
		current += regen;
		ManaRegainEvent event = new ManaRegainEvent(character);
		if (current >= max) {
			event.setNewVal(max);
		} else {
			event.setNewVal(current);
			event.setAmount(regen);
		}
		NtRpgPlugin.GlobalScope.game.getEventManager().post(event);
		event.getCharacter().getMana().setValue(event.getNewVal());
		Gui.displayMana(character);
	}

	@Override
	public int getStacks() {
		return 1;
	}

	@Override
	public void setStacks(int level) {

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
