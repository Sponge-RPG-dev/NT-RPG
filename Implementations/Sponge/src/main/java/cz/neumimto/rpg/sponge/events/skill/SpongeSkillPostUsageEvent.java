

package cz.neumimto.rpg.sponge.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillPostUsageEvent;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SpongeSkillPostUsageEvent extends AbstractSkillEvent implements SkillPostUsageEvent {

    private IEntity caster;

    @Override
    public IEntity getCaster() {
        return caster;
    }

    @Override
    public void setCaster(IEntity caster) {
        this.caster = caster;
    }
}