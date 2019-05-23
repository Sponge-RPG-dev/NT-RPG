package cz.neumimto.rpg.events.skill;

import cz.neumimto.rpg.api.skills.ISkill;
import org.spongepowered.api.event.Event;

public interface SkillEvent extends Event {
	ISkill getSkill();
}
