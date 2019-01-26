package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public class Custom extends AbstractLevelprogression {

    @Override
    public double[] initCurve() {
        return null;
    }
}
