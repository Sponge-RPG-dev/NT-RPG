package cz.neumimto.rpg.effects;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;

/**
 * Created by NeumimTo on 29.7.2017.
 */
public abstract class ShapedEffectDecorator<Value> extends EffectBase<Value> {

    int iter = 0;
    private int printerCount;
    private int q = 0;

    public ShapedEffectDecorator(String name, IEffectConsumer consumers) {
        super(name, consumers);
        setPeriod(7L);
    }

    @Override
    public void onTick(IEffect self) {
        int i = getVertices().length / printerCount;
        for (int j = 0; j <= printerCount - 1; j++) {
            int v = i * j + iter;
            draw(getVertices()[getIndex(v)]);

        }
        iter++;
    }

    public abstract void draw(Vector3d vec);

    public abstract Vector3d[] getVertices();


    public int getIndex(int i) {
        if (i < 0) {
            return 0;
        }
        if (i > getVertices().length - 1) {
            while (i > getVertices().length - 1) {
                i -= getVertices().length - 1;
            }
            return i;
        }
        return i;
    }

    public void setPrinterCount(int printerCount) {
        this.printerCount = printerCount;
        this.q = printerCount;
    }
}
