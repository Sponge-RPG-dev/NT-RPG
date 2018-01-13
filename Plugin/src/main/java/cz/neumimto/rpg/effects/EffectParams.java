package cz.neumimto.rpg.effects;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;

import java.util.HashMap;

public class EffectParams extends HashMap<String, String> implements DataSerializable {
    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }
}
