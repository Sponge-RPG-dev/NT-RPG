

package cz.neumimto.rpg.sponge.events.skill;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillFinishedEvent;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SpongeSkillFinishedEvent extends AbstractSkillEvent implements SkillFinishedEvent {

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