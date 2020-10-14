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

package cz.neumimto.rpg.sponge.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.effects.core.DefaultManaRegeneration;
import cz.neumimto.rpg.common.entity.players.CharacterMana;

/**
 * Created by ja on 4.9.2015.
 */
@Generate(id = "name", description = "An effect which will redirect all skill's mana consumption to the health pool")
public class BloodMagicEffect extends EffectBase {

    public static String name = "BloodMagic";
    private IActiveCharacter consumer;

    public BloodMagicEffect(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        this.consumer = (IActiveCharacter) consumer;
        setDuration(duration);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply(IEffect self) {
        Gui.sendEffectStatus(consumer, EffectStatusType.APPLIED, this);
        consumer.removeEffect(DefaultManaRegeneration.name);
        IReservable health = (IReservable) consumer.getHealth();
        consumer.setMana(health);
    }


    @Override
    public void onRemove(IEffect self) {
        Gui.sendEffectStatus(consumer, EffectStatusType.EXPIRED, this);
        consumer.setMana(new CharacterMana(consumer));
        //todo re-add mana regain event, or set period of mana regen to long.maxval; + listener
    }

}
