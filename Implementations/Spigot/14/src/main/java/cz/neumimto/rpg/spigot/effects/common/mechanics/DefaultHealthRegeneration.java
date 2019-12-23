package cz.neumimto.rpg.spigot.effects.common.mechanics;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.4.17.
 */
@Generate(id = "name", description = "A component which enables default health regeneration")
public class DefaultHealthRegeneration extends EffectBase implements IEffectContainer {

    public static final String name = "DefaultHealthRegen";

    public DefaultHealthRegeneration(IEffectConsumer character) {
        super(name, character);
    }

    @Override
    public Set<DefaultHealthRegeneration> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }


    @Override
    public DefaultHealthRegeneration constructEffectContainer() {
        return this;
    }

    @Override
    public Object getStackedValue() {
        return null;
    }

    @Override
    public void setStackedValue(Object o) {

    }
}
