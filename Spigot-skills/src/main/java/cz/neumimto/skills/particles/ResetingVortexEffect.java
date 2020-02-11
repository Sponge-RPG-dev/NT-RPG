package cz.neumimto.skills.particles;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.VortexEffect;

public class ResetingVortexEffect extends VortexEffect {
    private final float maxy;

    public ResetingVortexEffect(EffectManager effectManager, float maxy) {
        super(effectManager);
        this.maxy = maxy;
    }
    @Override
    public void onRun() {
        if (step * grow > maxy) {
            reset();
        }
        super.onRun();
    }

}
