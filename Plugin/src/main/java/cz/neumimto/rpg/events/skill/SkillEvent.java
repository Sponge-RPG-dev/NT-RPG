package cz.neumimto.rpg.events.skill;

import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.Event;

public interface SkillEvent extends Event {
	ISkill getSkill();
}
