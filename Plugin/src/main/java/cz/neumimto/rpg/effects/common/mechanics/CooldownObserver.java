package cz.neumimto.rpg.effects.common.mechanics;


import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 26.4.17.
 */
@Generate(id = "name", description = "A component which will be displaying cooldowns in the action bar")
public class CooldownObserver extends EffectBase implements IEffectContainer {

    public static final String name = "CooldownObserver";

    private IActiveCharacter character;


    public CooldownObserver(IEffectConsumer character, long duration, String value) {
        super(name, character);
        this.character = (IActiveCharacter) character;
        setDuration(duration);
        String s = Utils.extractNumber(value);
        long l = 1000;
        if (s != null) {
            l = Long.parseLong(s);
            if (value.endsWith("s")) {
                l *= 1000;
            }
        }
        setPeriod(l);
    }


    @Override
    public void onTick(IEffect self) {
        Long time = System.currentTimeMillis();
        Map<String, Long> cooldowns = character.getCooldowns();
        long p;
        Text.Builder builder = null;
        for (Map.Entry<String, Long> a : cooldowns.entrySet()) {
            p = a.getValue() - time;
            if (p > 120 && p < 0) {
                if (builder == null) {
                    builder = Text.builder(a.getKey()).color(TextColors.GREEN)
                            .append(Text.builder(":").color(TextColors.WHITE).build())
                            .append(Text.builder(" " + p).color(p > 10 ? TextColors.RED : TextColors.YELLOW).build())
                            .append(Text.builder("; ").color(TextColors.WHITE).build());
                } else {
                    builder.append(Text.builder(a.getKey()).color(TextColors.GREEN)
                            .append(Text.builder(":").color(TextColors.WHITE).build())
                            .append(Text.builder(" " + p).color(p > 10 ? TextColors.RED : TextColors.YELLOW).build())
                            .append(Text.builder("; ").color(TextColors.WHITE).build()).build());
                }
            }
        }
        if (builder != null) {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, builder.build());
        }
    }

    @Override
    public Set<CooldownObserver> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }


    @Override
    public CooldownObserver constructEffectContainer() {
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
