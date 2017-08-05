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
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder;

/**
 * Created by NeumimTo on 29.12.2015.
 */
public class SkillDamageSourceBuilder extends AbstractDamageSourceBuilder<SkillDamageSource, SkillDamageSourceBuilder> {

    protected ISkill skill;
    protected IEntity caster;
    protected IEntity target;
    protected IEffect effect;

    public ISkill getSkill() {
        return skill;
    }

    public SkillDamageSourceBuilder setSkill(ISkill skill) {
        this.skill = skill;
        return this;
    }

    public SkillDamageSourceBuilder fromSkill(ISkill skill) {
        this.skill = skill;
        type(skill.getDamageType());
        return this;
    }

    public IEntity getCaster() {
        return caster;
    }

    public SkillDamageSourceBuilder setCaster(IActiveCharacter caster) {
        this.caster = caster;
        return this;
    }

    @Override
    public SkillDamageSource build() throws IllegalStateException {
        return new SkillDamageSource(this);
    }

    public IEntity getTarget() {
        return target;
    }

    public SkillDamageSourceBuilder setTarget(IEntity target) {
        this.target = target;
        return this;
    }

    public SkillDamageSourceBuilder setCaster(IEntity caster) {
        this.caster = caster;
        return this;
    }

    public IEffect getEffect() {
        return effect;
    }

    public SkillDamageSourceBuilder setEffect(IEffect effect) {
        this.effect = effect;
        return this;
    }
}
