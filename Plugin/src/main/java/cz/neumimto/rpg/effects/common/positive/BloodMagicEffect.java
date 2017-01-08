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

package cz.neumimto.rpg.effects.common.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.common.def.ManaRegeneration;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.Health;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.Mana;

/**
 * Created by ja on 4.9.2015.
 */
@ClassGenerator.Generate(id = "name")
public class BloodMagicEffect extends EffectBase {

    public static String name = "BloodMagic";
    private static String apply = "You have gained " + name;
    private static String expire = "You have lost " + name;
    private IActiveCharacter consumer;
    public BloodMagicEffect(IActiveCharacter consumer) {
        super(name, consumer);
        setConsumer(consumer);
        setApplyMessage(apply);
        setExpireMessage(expire);
        setConsumer(consumer);
        setDuration(-1);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply() {
        Gui.sendEffectStatus(consumer, EffectStatusType.APPLIED, this);
        consumer.removeEffect(ManaRegeneration.name);
        Health health = consumer.getHealth();
        consumer.setMana(health);
    }


    @Override
    public void onRemove() {
        Gui.sendEffectStatus(consumer, EffectStatusType.EXPIRED, this);
        consumer.setMana(new Mana(consumer));
        consumer.addEffect(new ManaRegeneration(consumer));
    }

}
