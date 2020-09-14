package cz.neumimto.rpg.sponge.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by ja on 12.8.2017.
 */
public class Blackhole extends ActiveSkill {

    public static Vector3d[] arm = new Vector3d[0];

    @Inject
    private ParticleDecorator particleDecorator;

    @PostConstruct
    public void initArms() {
        arm = new Vector3d[75];
        int rot = 0;
		/*while (rot < 1) {
            new ParticleDecorator()
                    .spiral(
                            5,
                            32,
                            1,
                            rot,
                            vector3d -> );
            /*
            rot += 0.3;
        }
        */
    }

    @Override
    public SkillResult cast(IActiveCharacter character, PlayerSkillContext modifier) {
        return SkillResult.OK;
    }
}
