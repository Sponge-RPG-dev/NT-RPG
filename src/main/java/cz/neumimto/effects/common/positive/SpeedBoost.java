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

package cz.neumimto.effects.common.positive;

import cz.neumimto.ClassGenerator;
import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by NeumimTo on 23.7.2015.
 */

/**
 *  An example class how to use Classgenerator.
 *
 *  The annotation will generate according global effect class at runtime.
 *  id - field name of unique identifier (in most cases its name), the field must be static and public
 *  inject - If set to true the class loader tries to inject public static field which is assingable from IGlobalEffect.
 *  Main behavior of global effects is that their are accessible via effectservice.getGlobalEffect(stringId) inject option is
 *  here only if someone would like to keep direct field reference to the global effect object.
 *
 *  The class, which inherits from IEffect(or its implementations such as effect base) must contain a constructor - IEffectConsumer, long duration, int level.
 */
@ClassGenerator.Generate(id = "name",inject = true)
public class SpeedBoost extends EffectBase {

    public static final String name = "Speedboost";

    public static IGlobalEffect<SpeedBoost> global;

    @Override
    public String getName() {
        return name;
    }

    private float speedbonus;
    private IActiveCharacter character;

    public SpeedBoost(IActiveCharacter consumer, long duration, float speedbonus) {
        super(name, consumer);
        setDuration(duration);
        this.speedbonus = speedbonus;
        character = consumer;

    }

    @Override
    public void onStack(int level) {
        super.onStack(level);
    }

    @Override
    public void onApply() {
        super.onApply();
        character.setCharacterProperty(DefaultProperties.walk_speed, character.getCharacterProperty(DefaultProperties.walk_speed) + speedbonus);
        getGlobalScope().characterService.updateWalkSpeed(character);
        getConsumer().sendMessage(Localization.SPEED_BOOST_APPLY);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        character.setCharacterProperty(DefaultProperties.walk_speed, character.getCharacterProperty(DefaultProperties.walk_speed) - speedbonus);
        getGlobalScope().characterService.updateWalkSpeed(character);
        getConsumer().sendMessage(Localization.SPEED_BOOST_EXPIRE);
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }


}
