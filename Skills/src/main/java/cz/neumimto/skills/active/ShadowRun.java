package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.effects.positive.ShadowRunEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@ResourceLoader.Skill
public class ShadowRun extends ActiveSkill {

    public ShadowRun() {
        setName("ShadowRun");
        setDescription(SkillLocalization.SKILL_SHADOWRUN_DESC);
        setLore(SkillLocalization.SKILL_SHADOWRUN_LORE);
        addSkillType(SkillType.STEALTH);
        addSkillType(SkillType.MOVEMENT);
        addSkillType(SkillType.ESCAPE);
        SkillSettings settings = new SkillSettings();
        settings.addNode(SkillNodes.DURATION, 20000, 1750);
        settings.addNode(SkillNodes.DAMAGE, 10, 5);
        settings.addNode(SkillNodes.MULTIPLIER, 15, 8);
        settings.addNode("max-light-level", 12, -2);
        settings.addNode("walk-speed", 0.07f, 0.007f);
        setSettings(settings);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
        Location<World> location = character.getPlayer().getLocation();
        World extent = location.getExtent();
        Optional<GroundLuminanceProperty> property = location.getBlock().getProperty(GroundLuminanceProperty.class);
        GroundLuminanceProperty groundLuminanceProperty = property.get();
        double llevel = getDoubleNodeValue(info, "max-light-level");
        if (groundLuminanceProperty.getValue() <= llevel) {
            long duration = getLongNodeValue(info, SkillNodes.DURATION);
            double damage = getDoubleNodeValue(info, SkillNodes.DAMAGE);
            double attackmult = getDoubleNodeValue(info, SkillNodes.MULTIPLIER);
            double walkspeed = getDoubleNodeValue(info, "walk-speed");
            IEffect effect = new ShadowRunEffect(character, duration, damage, attackmult, walkspeed);
        }
        return SkillResult.CANCELLED;
    }
}
