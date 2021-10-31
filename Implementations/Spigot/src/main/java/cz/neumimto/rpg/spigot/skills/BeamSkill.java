package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.utils.Beam;

public abstract class BeamSkill extends ActiveSkill<ISpigotCharacter> {

    protected Beam.OnTick onTick;
    protected Beam.OnEntityHit onEntityHit;
    protected Beam.OnHitGround onHitGround;
    protected double step = 0.5;
    protected double gravity;
    protected double maxDistance;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DISTANCE, "10 + level");
        settings.addExpression(SkillNodes.GRAVITY, "0");
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info) {
        new Beam(character, step,
                info.getDoubleNodeValue(SkillNodes.GRAVITY),
                info.getDoubleNodeValue(SkillNodes.DISTANCE),
                info,
                onTick, onEntityHit, onHitGround);
        return SkillResult.OK;
    }

}
