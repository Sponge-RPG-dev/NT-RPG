
/*  Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
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
 */

package cz.neumimto.rpg.sponge.skills.types;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillTargetAttemptEvent;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.types.ITargeted;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.living.Living;

import javax.inject.Inject;

public abstract class Targeted extends ActiveSkill<ISpongeCharacter> implements ITargeted<ISpongeCharacter> {

    @Inject
    protected SpongeDamageService damageService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.RANGE, 10);
    }

    @Override
    public SkillResult cast(ISpongeCharacter caster, PlayerSkillContext skillContext) {
        int range = skillContext.getIntNodeValue(SkillNodes.RANGE);
        Living l = Utils.getTargetedEntity(caster, range);
        if (l == null) {
            if (getDamageType() == null && !getSkillTypes().contains(SkillType.CANNOT_BE_SELF_CASTED)) {
                l = caster.getEntity();
            } else {
                return SkillResult.NO_TARGET;
            }
        }
        if (getDamageType() != null && !damageService.canDamage(caster, l)) {
            return SkillResult.CANCELLED;
        }
        IEntity target = Rpg.get().getEntityService().get(l);

        SkillTargetAttemptEvent event = Rpg.get().getEventFactory().createEventInstance(SkillTargetAttemptEvent.class);
        event.setSkill(this);
        event.setCaster(caster);
        event.setTarget(target);

        if (Rpg.get().postEvent(event)) {
            //todo https://github.com/Sponge-RPG-dev/NT-RPG/issues/111
            return SkillResult.CANCELLED;
        }
        return castOn(event.getTarget(), (ISpongeCharacter) event.getCaster(), skillContext);
    }
}
