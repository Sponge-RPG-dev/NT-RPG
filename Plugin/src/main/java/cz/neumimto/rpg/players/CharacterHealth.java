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

package cz.neumimto.rpg.players;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.entities.IReservable;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;
import org.spongepowered.api.data.key.Keys;

/**
 * Created by NeumimTo on 30.12.2014.
 */
public class CharacterHealth implements IReservable {

    private final IActiveCharacter activeCharacter;

    public CharacterHealth(IActiveCharacter activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    @Override
    public double getMaxValue() {
        return activeCharacter.getPlayer().get(Keys.MAX_HEALTH).get();
    }

    @Override
    public void setMaxValue(double f) {
        activeCharacter.getPlayer().offer(Keys.MAX_HEALTH, f);
    }

    //todo useservice instead
    //todo implement reserved amounts
    @Override
    public void setReservedAmnout(float f) {
        activeCharacter.setProperty(SpongeDefaultProperties.reserved_health, f);
    }

    @Override
    public double getReservedAmount() {
        return NtRpgPlugin.GlobalScope.entityService.getEntityProperty(activeCharacter, SpongeDefaultProperties.reserved_health);
    }

    @Override
    public double getValue() {
        return activeCharacter.getPlayer().get(Keys.HEALTH).get();
    }

    @Override
    public void setValue(double f) {
        activeCharacter.getPlayer().offer(Keys.HEALTH, f);
    }

    @Override
    public double getRegen() {
        return NtRpgPlugin.GlobalScope.entityService.getEntityProperty(activeCharacter, SpongeDefaultProperties.health_regen);
    }

    @Override
    public void setRegen(float f) {
        activeCharacter.setProperty(SpongeDefaultProperties.health_regen, f);
    }
}
