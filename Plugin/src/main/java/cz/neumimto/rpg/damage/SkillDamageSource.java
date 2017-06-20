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
package cz.neumimto.rpg.damage;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSource;

/**
 * Created by NeumimTo on 29.12.2015.
 */
public class SkillDamageSource extends AbstractDamageSource implements ISkillDamageSource {

    private ISkill skill;
    private IEntity caster;
    private IEntity target;
    private IEffect effect;

    public SkillDamageSource(SkillDamageSourceBuilder builder) {
        super(builder);
        this.skill = builder.getSkill();
        this.caster = builder.getCaster();
        this.target = builder.getTarget();
        this.effect = builder.getEffect();
    }

    @Override
    public final ISkill getSkill() {
        return this.skill;
    }

    @Override
    public IEntity getCaster() {
        return caster;
    }

    @Override
    public IEntity getTarget() {
        return target;
    }

    @Override
    public IEffect getEffect() {
        return effect;
    }

}