package cz.neumimto.skills;

import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ITargetted {
    public boolean castOn(Living target, SkillSource source, int skillLevel, SkillModifiers mods);
}
